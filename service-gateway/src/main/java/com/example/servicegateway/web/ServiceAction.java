package com.example.servicegateway.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

//@RestController
//@RequestMapping(value = "/service")
@Slf4j
public class ServiceAction {
    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/**")
    public ResponseEntity<byte[]> doService(HttpServletRequest request,
                                            HttpEntity<byte[]> requestEntity, HttpMethod httpMethod) throws Exception {
        String requestURI = request.getRequestURI();

        URI normalize = new URI(requestURI).normalize();
        String contextPath = request.getContextPath();
        String path = normalize.toString().substring((contextPath + "service/").length());
        String restURI = DiscoverClient.getRestURI("rest","",path);
        log.info(restURI);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                restURI,
                httpMethod, requestEntity, byte[].class);
        return responseEntity;
    }
}
