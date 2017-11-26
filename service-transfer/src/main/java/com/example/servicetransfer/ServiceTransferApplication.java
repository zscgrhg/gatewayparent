package com.example.servicetransfer;

import com.example.servicetransfer.web.Locator;
import com.example.servicetransfer.web.LocatorImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ServletComponentScan
public class ServiceTransferApplication {
	@Bean
	public Locator locator(){
		return new LocatorImpl();
	}
	public static void main(String[] args) {
		SpringApplication.run(ServiceTransferApplication.class, args);
	}
}
