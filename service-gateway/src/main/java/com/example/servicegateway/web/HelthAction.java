package com.example.servicegateway.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelthAction {
    @RequestMapping("hello")
    public String welcome(){
        return "welcome to gateway!";
    }
}
