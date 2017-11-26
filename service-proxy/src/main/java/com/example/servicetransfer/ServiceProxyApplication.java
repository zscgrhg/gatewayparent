package com.example.servicetransfer;

import com.example.servicetransfer.web.Locator;
import com.example.servicetransfer.web.LocatorImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
@ServletComponentScan
public class ServiceProxyApplication {


    public static void main(String[] args) {
        SpringApplication.run(ServiceProxyApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        //RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                request.getHeaders().add("TraceId", UUID.randomUUID().toString());
                return execution.execute(request, body);
            }
        }));
        return restTemplate;
    }

    @Bean
    public Locator locator() {
        return new LocatorImpl();
    }
}
