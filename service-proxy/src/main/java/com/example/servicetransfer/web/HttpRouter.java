package com.example.servicetransfer.web;

import com.example.servicetransfer.web.ssl.TrustAllManager;
import com.example.servicetransfer.web.ssl.WhitelistVerifier;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
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
public class HttpRouter {

    private static final int BUFFER_SIZE = 4096;
    private static final String HTTP_POST = "POST";

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

    public void forward(Locator locator, HttpServletRequest req, HttpServletResponse resp)
            throws UnavailableException, IOException {
        boolean withBody = HTTP_POST.equals(req.getMethod().toUpperCase());
        forward(req, resp, locator, withBody);
    }

    public void forward(URL resourceURL, HttpServletRequest req, HttpServletResponse resp)
            throws UnavailableException, IOException {
        boolean withBody = HTTP_POST.equals(req.getMethod().toUpperCase());
        forwardHttp(req, resp, resourceURL, withBody);
    }


    private void forward(HttpServletRequest req, HttpServletResponse resp, Locator locator, boolean withBody)
            throws IOException, UnavailableException {
        try {
            URI relativeURI = extractURI(req);
            URL resourceURL = locator.locate(relativeURI);
            String protocol = resourceURL.getProtocol();
            switch (protocol) {
                case "http":
                case "https":
                    forwardHttp(req, resp, resourceURL, withBody);
                    break;
                default:
                    throw new ProtocolException("unsupported protocol:" + protocol);
            }
        } catch (URISyntaxException e) {
            throw new UnavailableException(e.getMessage());
        }
    }


    private void forwardHttp(HttpServletRequest req, HttpServletResponse resp, URL resourceURL, boolean withBody)
            throws IOException {
        String method = req.getMethod();
        HttpURLConnection conn = (HttpURLConnection) resourceURL.openConnection();
        if (withBody) {
            conn.setDoOutput(true);
        }
        conn.setDoInput(true);
        conn.setRequestMethod(method);
        copyHeaders(req, conn);
        conn.setInstanceFollowRedirects(false);
        conn.connect();
        if (withBody) {
            copy(req.getInputStream(), conn.getOutputStream());
        }
        copyHeaders(conn, resp);
        int respCode = conn.getResponseCode();
        resp.setStatus(respCode);
        InputStream error = conn.getErrorStream();
        if (error != null) {
            copy(error, resp.getOutputStream());
        } else {
            copy(conn.getInputStream(), resp.getOutputStream());
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

    private URI extractURI(HttpServletRequest req) throws URISyntaxException {
        String requestURI = req.getRequestURI();
        String queryString = req.getQueryString();
        if (queryString != null) {
            requestURI += "?" + queryString;
        }

        URI normalize = new URI(requestURI).normalize();
        String contextPath = req.getContextPath();
        String concat = contextPath + req.getServletPath();
        String relativeURI = normalize.toString().substring(concat.length());
        return new URI(relativeURI);
    }
}
