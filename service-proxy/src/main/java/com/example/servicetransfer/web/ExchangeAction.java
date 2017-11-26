package com.example.servicetransfer.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

import static com.example.servicetransfer.web.ExchangeAction.MAPPING;

@RestController
@RequestMapping(MAPPING)
@Slf4j
@Deprecated//不支持文件上传
public class ExchangeAction {

    public static final String MAPPING = "/exchange";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Locator locator;

    @RequestMapping(value = "/{name}/{version}/**")
    public ResponseEntity<byte[]> doService(HttpServletRequest request,
                                            HttpEntity<byte[]> requestEntity,
                                            HttpMethod httpMethod,
                                            @PathVariable("name") String name,
                                            @PathVariable("version") String version
    ) throws Exception {
        String path = request.getPathInfo();
        if (path == null || path.isEmpty()) {
            path = request.getServletPath();
        }
        String p = path.substring(MAPPING.length() + name.length() + version.length() + 2);
        URL resourceURL = locator.locate(name, version, p);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                resourceURL.toString(),
                httpMethod, requestEntity, byte[].class);
        return responseEntity;
    }
}
