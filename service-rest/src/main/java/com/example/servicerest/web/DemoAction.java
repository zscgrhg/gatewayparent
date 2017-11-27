package com.example.servicerest.web;

import com.example.servicerest.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RestController
public class DemoAction {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        User user = new User();
        user.setName("nihao123");

        user.setAddress("local");
        user.setAge(12);
        user.setSex("female");
        user.setBirthday(new Date());
        String json = mapper.writeValueAsString(user);
        System.out.println(json);
        System.out.println(json.length());
    }

    @RequestMapping(value = "hello")
    public String welcome(@RequestHeader MultiValueMap<String, String> headers, HttpServletRequest request) {
        return "welcome to rest demo!";
    }

    @RequestMapping("who")
    public User who(@RequestBody User user, @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response) {
        response.setHeader("abcdefg", "qqqqq");
        return user;
    }

    @RequestMapping("image")
    public void image(HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "public, max-age=604800000");
        response.setHeader("Last-Modified",
                "Sun, 26 Nov 2017 05:52:09 GMT");
        FileCopyUtils.copy(getClass().getClassLoader().getResourceAsStream("static\\girls.jpg"), response.getOutputStream());
    }

}
