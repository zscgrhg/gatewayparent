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
            throw new UnavailableException("");
        }
        String name = matcher.group("name");
        String version = matcher.group("version");
        String action = matcher.group("resource");
        String restURI = getRestURI(name, version, action);
        return new URI(restURI).normalize().toURL();
    }

    private String getRestURI(String name, String version, String path) {
        switch (name) {
            case "rest":
                return "http://127.0.0.1:8080/" + path;
            default:
                return "http://127.0.0.1:8081/" + path;
        }
    }
}
