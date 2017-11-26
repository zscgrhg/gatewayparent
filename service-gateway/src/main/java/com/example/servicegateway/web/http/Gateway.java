package com.example.servicegateway.web.http;

import com.example.servicegateway.web.DiscoverClient;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.example.servicegateway.web.GatewayServlet.MAPPING;

@WebServlet(MAPPING)
public class Gateway extends HttpServlet {
    public static final String MAPPING = "/service/*";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, true);
    }

    private void doForward(HttpServletRequest req, HttpServletResponse resp, boolean isPost) throws ServletException, IOException {
        try {
            URI dest = DiscoverClient.normalizeURI(req);
            URL url = dest.toURL();
            Map<String, String> headers = getHeaders(req);
            HttpResource resource = new HttpResourceImpl(url, headers, isPost);
            if (isPost) {
                FileCopyUtils.copy(req.getInputStream(), resource.getRequestStream());
            }
            Map<String, String> responseHeaders = resource.getResponseHeaders();
            resp.setStatus(resource.getResponseCode());
            responseHeaders.forEach((k, v) -> {
                resp.setHeader(k, v);
            });
            FileCopyUtils.copy(resource.getResponseStream(), resp.getOutputStream());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getHeaders(HttpServletRequest req) {
        Enumeration<String> headerNames = req.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, req.getHeader(headerName));
        }
        return headers;
    }
}
