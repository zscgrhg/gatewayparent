package com.example.servicerest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class User {
    private String name;
    private String sex;
    private int age;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
}
