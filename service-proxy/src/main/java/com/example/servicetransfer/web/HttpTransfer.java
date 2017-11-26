package com.example.servicetransfer.web;

import com.example.servicetransfer.web.ssl.TrustAllManager;
import com.example.servicetransfer.web.ssl.WhitelistVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;

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
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpTransfer {

    static {
        try {
            initSSL();
        } catch (Exception e) {
            log.error("Failed to Init SSLContext,https wont be supported! caused by:" + e.getMessage());
        }
    }

    private static void initSSL() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new WhitelistVerifier());
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public void transfer(HttpServletRequest req, HttpServletResponse resp, Locator locator, boolean withBody) throws URISyntaxException, IOException, UnavailableException {
        String resourcePath = ServletUtil.normalizeURI(req);
        URL resourceURL = locator.locate(resourcePath);
        String protocol = resourceURL.getProtocol();
        switch (protocol) {
            case "http":
            case "https":
                transferHttp(req, resp, resourceURL, withBody);
                break;
            default:
                transferDefault(req, resp, resourceURL);
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
            FileCopyUtils.copy(reqInput, outputStream);
            closeResource(outputStream);
        }
        copyHeaders(connection, resp);
        int responseCode = connection.getResponseCode();
        resp.setStatus(responseCode);
        InputStream errorStream = connection.getErrorStream();
        InputStream inputStream = connection.getInputStream();
        if (errorStream != null) {
            FileCopyUtils.copy(errorStream, resp.getOutputStream());
            closeResource(errorStream);
        } else {
            FileCopyUtils.copy(inputStream, resp.getOutputStream());
            closeResource(inputStream);
        }
    }

    private void transferDefault(HttpServletRequest req, HttpServletResponse resp, URL resourceURL) {
        throw new UnsupportedOperationException();
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
            if (name != null) {
                to.addRequestProperty(name, from.getHeader(name));
            }
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

}
