package com.example.servicetransfer.web;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public class ServletUtil {


    public static String normalizeURI(HttpServletRequest req, String handlerMapping) throws URISyntaxException {

        return normalizeResourceURI(req, handlerMapping);
    }

    public static String normalizeURI(HttpServletRequest req) throws URISyntaxException {

        return normalizeResourceURI(req, req.getServletPath());
    }

    private static String normalizeResourceURI(HttpServletRequest req, String handlerMapping) throws URISyntaxException {
        String requestURI = req.getRequestURI();
        String queryString = req.getQueryString();
        if (queryString != null) {
            requestURI += "?" + queryString;
        }
        URI normalize = new URI(requestURI).normalize();
        String contextPath = req.getContextPath();
        String concat = contextPath + handlerMapping;
        String path = normalize.toString().substring(concat.length());
        return path;
    }
}
