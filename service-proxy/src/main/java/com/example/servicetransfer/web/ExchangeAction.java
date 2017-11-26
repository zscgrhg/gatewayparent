package com.example.servicetransfer.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

import static com.example.servicetransfer.web.ExchangeAction.MAPPING;

@RestController
@RequestMapping(MAPPING)
@Slf4j
public class ExchangeAction {

    public static final String MAPPING = "/exchange";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Locator locator;

    @RequestMapping(value = "/**")
    public ResponseEntity<byte[]> doService(HttpServletRequest request,
                                            HttpEntity<byte[]> requestEntity, HttpMethod httpMethod) throws Exception {
        String resourcePath = ServletUtil.normalizeURI(request, MAPPING);
        URL resourceURL = locator.locate(resourcePath);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                resourceURL.toString(),
                httpMethod, requestEntity, byte[].class);
        return responseEntity;
    }
}
