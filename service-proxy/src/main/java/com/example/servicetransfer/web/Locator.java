package com.example.servicetransfer.web;

import javax.servlet.UnavailableException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public interface Locator {
    URL locate(URI resource) throws UnavailableException, URISyntaxException, MalformedURLException;

    URL locate(String name, String version, String path) throws UnavailableException, URISyntaxException, MalformedURLException;
}
