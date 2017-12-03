package com.example.servicerest.web;

import com.example.servicerest.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
    public void welcome(@RequestHeader MultiValueMap<String, String> headers, HttpServletRequest request
            , HttpServletResponse response) throws InterruptedException, IOException {

        String data = "welcome to rest demo!welcome to rest demo!welcome to rest demo!welcome to rest demo!welcome to rest demo!welcome to rest demo!welcome to rest demo!welcome to rest demo!welcome to rest demo!welcome to rest demo!";
        PrintWriter writer = response.getWriter();
        response.setBufferSize(10240);
        for (int i = 0; i < 20; i++) {
            Thread.sleep(10);
            if (i % 8 == 0) {

            } else {
                writer.write(data);
            }
            writer.flush();
        }
    }

    @RequestMapping("who")
    public User who(@RequestBody User user, @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response) {
        response.setHeader("abcdefg", "qqqqq");
        return user;
    }


}
