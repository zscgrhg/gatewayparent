package com.example.servicetransfer.web;

import javax.servlet.UnavailableException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocatorImpl implements Locator {
    private static final Pattern URI_PATTERN = Pattern.compile("/(?<name>[^/]+)/(?<version>[^/]+)/(?<resource>.*)");

    @Override
    public URL locate(String resource) throws UnavailableException, URISyntaxException, MalformedURLException {

        Matcher matcher = URI_PATTERN.matcher(resource);
        boolean matches = matcher.matches();
        if (!matches) {
            throw new UnavailableException("The requested resource is not available");
        }
        String name = matcher.group("name");
        String version = matcher.group("version");
        String action = matcher.group("resource");
        return locate(name, version, action);
    }


    public URL locate(String name, String version, String action) throws UnavailableException, URISyntaxException, MalformedURLException {
        switch (name) {
            case "public":
                return new URI("http://cn.bing.com/" + action).normalize().toURL();

            case "rest":
                return new URI("http://localhost:8080/" + action).normalize().toURL();
            default:
                return new URI("http://localhost:8081/" + action).normalize().toURL();
        }
    }
}
