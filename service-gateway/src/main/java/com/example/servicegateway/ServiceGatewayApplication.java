package com.example.servicegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.support.HttpAccessor;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ServiceGatewayApplication {
	@Bean
	public RestTemplate okhttpRestTemplate(){
		return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
	}

	public static void main(String[] args) {
		SpringApplication.run(ServiceGatewayApplication.class, args);
	}
}
