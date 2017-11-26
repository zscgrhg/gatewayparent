package com.example.servicetransfer.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.example.servicetransfer.web.ExpressServlet.RESOURCE_MAPPING;
import static com.example.servicetransfer.web.ExpressServlet.SERVICE_MAPPING;

/**
 * Reverse Proxy
 */
@WebServlet({SERVICE_MAPPING, RESOURCE_MAPPING})
@Slf4j
public class ExpressServlet extends HttpServlet {

    public static final String RESOURCE_MAPPING = "/resource/*";
    public static final String SERVICE_MAPPING = "/service/*";

    public static final String RESOURCE_SERVLET_PATH = RESOURCE_MAPPING.substring(0, RESOURCE_MAPPING.length() - 2);
    private static final HttpTransfer transfer = new HttpTransfer();

    @Autowired
    private Locator locator;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (RESOURCE_SERVLET_PATH.equalsIgnoreCase(req.getServletPath()) &&
                (req.getHeader("If-Modified-Since") != null ||
                        req.getHeader("If-None-Match") != null)) {
            resp.setStatus(304);
        } else {
            doTransfer(req, resp, locator, false);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doTransfer(req, resp, locator, true);
    }


    private void doTransfer(HttpServletRequest req, HttpServletResponse resp, Locator locator, boolean withBody) throws IOException, UnavailableException {
        try {
            transfer.transfer(req, resp, locator, withBody);
        } catch (URISyntaxException e) {
            throw new UnavailableException(e.getMessage());
        }
    }
}
