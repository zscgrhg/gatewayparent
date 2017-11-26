package com.example.servicetransfer.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpTransfer {


    public void transfer(HttpServletRequest req, HttpServletResponse resp, Locator locator) throws URISyntaxException, IOException, UnavailableException {
        String resourcePath = normalizeURI(req);
        URL resourceURL = locator.locate(resourcePath);
        String protocol = resourceURL.getProtocol();
        switch (protocol) {
            case "http":
                transferHttp(req, resp, resourceURL);
                break;
            case "https":
                transferHttps(req, resp, resourceURL);
                break;
            default:
                transferDefault(req, resp, resourceURL);
        }
    }

    private void transferHttp(HttpServletRequest req, HttpServletResponse resp, URL resourceURL) throws IOException {
        String method = req.getMethod();
        HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        copyHeaders(req, connection);
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        OutputStream outputStream = connection.getOutputStream();
        ServletInputStream reqInput = req.getInputStream();
        if (reqInput != null) {
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

    private void transferHttps(HttpServletRequest req, HttpServletResponse resp, URL resourceURL) {
        throw new UnsupportedOperationException();
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
            to.addRequestProperty(name, from.getHeader(name));
        }
    }

    private void copyHeaders(HttpURLConnection from, HttpServletResponse to) {
        Map<String, List<String>> headerFields = from.getHeaderFields();
        headerFields.keySet().forEach(k -> {
            to.setHeader(k, from.getHeaderField(k));
        });
    }


    private String normalizeURI(HttpServletRequest req) throws URISyntaxException {
        String requestURI = req.getRequestURI();
        String queryString = req.getQueryString();
        if(queryString!=null){
            requestURI+="?"+queryString;
        }
        URI normalize = new URI(requestURI).normalize();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String path = normalize.toString().substring((contextPath + servletPath).length());
        return path;
    }
}
