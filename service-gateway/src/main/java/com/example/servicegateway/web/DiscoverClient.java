package com.example.servicegateway.web;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscoverClient {

    private static final Pattern URI_PATTERN = Pattern.compile("/(?<name>[^/]+)/(?<version>[^/]+)/(?<resource>.*)");

    public static URI normalizeURI(HttpServletRequest req) throws URISyntaxException, ServletException {
        String requestURI = req.getRequestURI();
        String queryString = req.getQueryString();
        URI normalize = new URI(requestURI).normalize();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String path = normalize.toString().substring((contextPath + servletPath).length());
        Matcher matcher = URI_PATTERN.matcher(path);
        boolean matches = matcher.matches();
        if (!matches) {
            throw new UnavailableException("");
        }
        String name = matcher.group("name");
        String version = matcher.group("version");
        String resource = matcher.group("resource");
        String restURI = DiscoverClient.getRestURI(name, version, resource);
        if(queryString==null||queryString.trim().isEmpty()){
            return new URI(restURI);
        }else {
            return new URI(restURI+"?"+queryString);
        }
    }



    public static String getRestURI(String name,String version,String path){
        switch (name){
            case "rest":
                return "http://127.0.0.1:8080/"+path;
            default:
                return "http://127.0.0.1:8081/"+path;
        }
    }


}
