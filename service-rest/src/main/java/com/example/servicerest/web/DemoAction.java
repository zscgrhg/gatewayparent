package com.example.servicerest.web;

import com.example.servicerest.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class DemoAction {
    @RequestMapping("hello")
    public String welcome(@RequestHeader MultiValueMap<String,String> headers){
        return "welcome to rest demo!";
    }
    @RequestMapping("who")
    public User who(@RequestBody User user, @RequestHeader MultiValueMap<String,String> headers, HttpServletResponse response){
        response.setHeader("this is a header","this is z value");
        return user;
    }


    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        User user = new User();
        user.setName("nihao123");
        user.setAddress("localhost");
        user.setAge(12);
        user.setSex("female");
        user.setBirthday(new Date());
        String json = mapper.writeValueAsString(user);
        System.out.println(json);
    }
}
