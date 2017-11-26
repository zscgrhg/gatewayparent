package com.example.servicetransfer.web;

import javax.servlet.UnavailableException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public interface Locator {
      URL locate(String resource) throws UnavailableException, URISyntaxException, MalformedURLException;
}
