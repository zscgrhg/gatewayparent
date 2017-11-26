package com.example.servicegateway.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import static com.example.servicegateway.web.GatewayServlet.MAPPING;


public class GatewayServlet extends HttpServlet {
    public static final String MAPPING = "/service/*";


    private static final Pattern URI_PATTERN = Pattern.compile("/(?<name>[^/]+)/(?<version>[^/]+)/(?<resource>.*)");

    private static URI normalizeURI(HttpServletRequest req) throws URISyntaxException, ServletException {
        String requestURI = req.getRequestURI();
        String queryString = req.getQueryString();
        System.out.println("qstr="+queryString);
        URI normalize = new URI(requestURI).normalize();
        String contextPath = req.getContextPath();
        String path = normalize.toString().substring((contextPath + MAPPING).length() - 2);
        Matcher matcher = URI_PATTERN.matcher(path);
        boolean matches = matcher.matches();
        if (!matches) {
            throw new UnavailableException("");
        }
        String name = matcher.group("name");
        String version = matcher.group("version");
        String resource = matcher.group("resource");
        String restURI = DiscoverClient.getRestURI(name, version, resource);
        return new URI(restURI+"?"+queryString);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.POST);
    }

    private void doForward(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod) throws ServletException, IOException {
        try {
            System.out.println(req.getRequestURI());
            System.out.println(req.getContextPath());
            System.out.println(req.getServletPath());
            URI dest = normalizeURI(req);
            RequestEntity.BodyBuilder builder = RequestEntity.post(dest);
            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                builder.header(headerName, req.getHeader(headerName));
            }
            ServletInputStream inputStream = req.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrStream();
            FileCopyUtils.copy(inputStream, byteArrayOutputStream);
            RequestEntity<byte[]> requestEntity = builder.body(byteArrayOutputStream.toByteArray());
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                    dest,
                    httpMethod, requestEntity, byte[].class);
            byte[] body = responseEntity.getBody();
            MultiValueMap<String, String> headers = responseEntity.getHeaders();
            boolean isText = false;
            for (Map.Entry<String, String> entry : headers.toSingleValueMap().entrySet()) {
                String headerName = entry.getKey();
                String value = entry.getValue();
                if (!"Transfer-Encoding".equalsIgnoreCase(headerName)) {
                    resp.setHeader(headerName, value);
                }
                if ("Content-Type".equalsIgnoreCase(headerName)) {
                    isText = value.toLowerCase().startsWith("text");
                }
            }
            resp.setStatus(responseEntity.getStatusCodeValue());

            if (isText &&
                    body.length > 2048 &&
                    req.getHeader("accept-encoding").toLowerCase().contains("gzip")
                    ) {
                //Transfer-Encoding
                resp.setHeader("content-encoding", "gzip");
                FileCopyUtils.copy(body, new GZIPOutputStream(resp.getOutputStream()));
            } else {
                FileCopyUtils.copy(body, resp.getOutputStream());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
