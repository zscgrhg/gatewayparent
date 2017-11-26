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

@WebServlet("/proxy/*")
public class UploadServlet extends HttpServlet {



    @Autowired
    private RestTemplate restTemplate;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            URI dest = new URI("http://127.0.0.1:8081/upload");
            RequestEntity.BodyBuilder builder = RequestEntity.post(dest);
            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                builder.header(headerName,req.getHeader(headerName));
            }
            ServletInputStream inputStream = req.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrStream();
            FileCopyUtils.copy(inputStream, byteArrayOutputStream);
            RequestEntity<byte[]> requestEntity = builder.body(byteArrayOutputStream.toByteArray());
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                    dest,
                    HttpMethod.POST, requestEntity, byte[].class);
            System.out.println(new String(responseEntity.getBody(),"UTF-8"));
            MultiValueMap<String,String> headers = responseEntity.getHeaders();
            for (Map.Entry<String, String> entry : headers.toSingleValueMap().entrySet()) {
                resp.setHeader(entry.getKey(),entry.getValue());
            }
            resp.setStatus(responseEntity.getStatusCodeValue());
            FileCopyUtils.copy(responseEntity.getBody(),resp.getOutputStream());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
