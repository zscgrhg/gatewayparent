package com.example.servicetransfer.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.servicetransfer.web.GatewayServlet.MAPPING;

@WebServlet(MAPPING)
@Slf4j
public class GatewayServlet extends HttpServlet {
    public static final String MAPPING = "/service/*";

    @Autowired
    private Locator locator;
    private HttpTransfer transfer = new HttpTransfer();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doTransfer(req, resp, locator);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doTransfer(req, resp, locator);
    }

    private void doTransfer(HttpServletRequest req, HttpServletResponse resp, Locator locator) throws IOException {
        try {
            transfer.transfer(req, resp, locator);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resp.sendError(500,e.getMessage());
        }
    }
}
