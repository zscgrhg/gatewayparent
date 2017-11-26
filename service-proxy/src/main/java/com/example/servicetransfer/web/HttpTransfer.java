package com.example.servicetransfer.web;

import com.example.servicetransfer.web.ssl.TrustAllManager;
import com.example.servicetransfer.web.ssl.WhitelistVerifier;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletInputStream;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpTransfer {

    private static final int BUFFER_SIZE = 4096;

    static {
        try {
            initSSL();
        } catch (Exception e) {
            log.error("Init SSLContext failed,Can not use https protocol! Caused by:" + e.getMessage());
        }
    }

    private static void initSSL() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new WhitelistVerifier());
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public void transfer(HttpServletRequest req, HttpServletResponse resp, Locator locator, boolean withBody) throws URISyntaxException, IOException, UnavailableException {
        String resourcePath = extractURI(req);
        URL resourceURL = locator.locate(resourcePath);
        String protocol = resourceURL.getProtocol();
        switch (protocol) {
            case "http":
            case "https":
                transferHttp(req, resp, resourceURL, withBody);
                break;
            default:
                throw new ProtocolException("unsupported protocol:" + protocol);
        }
    }

    private void transferHttp(HttpServletRequest req, HttpServletResponse resp, URL resourceURL, boolean withBody) throws IOException {
        String method = req.getMethod();
        HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();
        if (withBody) {
            connection.setDoOutput(true);
        }
        connection.setDoInput(true);
        connection.setRequestMethod(method);
        copyHeaders(req, connection);
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        if (withBody) {
            ServletInputStream reqInput = req.getInputStream();
            OutputStream outputStream = connection.getOutputStream();
            copy(reqInput, outputStream);
        }
        copyHeaders(connection, resp);
        int responseCode = connection.getResponseCode();
        resp.setStatus(responseCode);
        InputStream errorStream = connection.getErrorStream();
        InputStream inputStream = connection.getInputStream();
        if (errorStream != null) {
            copy(errorStream, resp.getOutputStream());
            closeResource(errorStream);
        } else {
            copy(inputStream, resp.getOutputStream());
        }
    }


    private int copy(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        closeResource(in);
        closeResource(out);
        return byteCount;
    }

    private void closeResource(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
    }

    private void copyHeaders(HttpServletRequest from, HttpURLConnection to) {
        Enumeration<String> headerNames = from.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            to.addRequestProperty(name, from.getHeader(name));
        }
        String xffHeaderName = "X-Forwarded-For";
        String xffHeader = from.getHeader(xffHeaderName);
        if (xffHeader == null) {
            to.addRequestProperty(xffHeaderName, from.getRemoteAddr());
        }
    }

    private void copyHeaders(HttpURLConnection from, HttpServletResponse to) {
        Map<String, List<String>> headerFields = from.getHeaderFields();
        headerFields.keySet().forEach(k -> {
            to.setHeader(k, from.getHeaderField(k));
        });
    }

    private String extractURI(HttpServletRequest req) throws URISyntaxException {
        String requestURI = req.getRequestURI();
        String queryString = req.getQueryString();
        if (queryString != null) {
            requestURI += "?" + queryString;
        }

        URI normalize = new URI(requestURI).normalize();
        String contextPath = req.getContextPath();
        String concat = contextPath + req.getServletPath();
        String action = normalize.toString().substring(concat.length());
        return action;
    }
}
