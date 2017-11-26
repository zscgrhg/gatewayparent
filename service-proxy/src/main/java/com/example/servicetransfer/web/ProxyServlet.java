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

import static com.example.servicetransfer.web.ProxyServlet.RESOURCE_MAPPING;
import static com.example.servicetransfer.web.ProxyServlet.SERVICE_MAPPING;

@WebServlet({SERVICE_MAPPING, RESOURCE_MAPPING})
@Slf4j
public class ProxyServlet extends HttpServlet {
    public static final String RESOURCE_SERVLET_PATH = "/resource";
    public static final String RESOURCE_MAPPING = "/resource/*";// =  RESOURCE_SERVLET_PATH +"/*"
    public static final String SERVICE_MAPPING = "/service/*";


    @Autowired
    private Locator locator;
    private HttpTransfer transfer = new HttpTransfer();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doTransfer(req, resp, locator, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doTransfer(req, resp, locator, true);
    }


    private void doTransfer(HttpServletRequest req, HttpServletResponse resp, Locator locator, boolean withBody) throws IOException, UnavailableException {
        try {

            if (RESOURCE_SERVLET_PATH.equalsIgnoreCase(req.getServletPath()) &&
                    (req.getHeader("If-Modified-Since") != null ||
                            req.getHeader("If-None-Match") != null)) {
                resp.setStatus(304);
            } else {
                transfer.transfer(req, resp, locator, withBody);
            }
        } catch (URISyntaxException e) {
            throw new UnavailableException(e.getMessage());
        }
    }
}
