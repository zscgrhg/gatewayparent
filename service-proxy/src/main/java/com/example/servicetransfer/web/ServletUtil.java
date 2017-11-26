package com.example.servicetransfer.web;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public class ServletUtil {

    public static String normalizeURI(HttpServletRequest req) throws URISyntaxException {
        String requestURI = req.getRequestURI();
        String queryString = req.getQueryString();
        if (queryString != null) {
            requestURI += "?" + queryString;
        }
        URI normalize = new URI(requestURI).normalize();
        String contextPath = req.getContextPath();
        String concat = contextPath + req.getServletPath();
        String path = normalize.toString().substring(concat.length());
        return path;
    }
}
