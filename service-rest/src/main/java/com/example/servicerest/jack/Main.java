package com.example.servicerest.jack;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class Main implements CommandLineRunner {
    @Autowired
    ObjectMapper mapper;

    public static GernericServiceData<User> newData() {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setName("user" + i);
            user.setAge(10 + i);
            user.setAdress("A00" + i);
            user.setPhone("0731-883376" + i);
            list.add(user);
        }
        Pager<User> userPager = new Pager<>();
        userPager.setData(list);
        userPager.setPage(1);
        userPager.setPageSize(5);
        userPager.setTotal(388);
        GernericServiceData<User> serviceData = new GernericServiceData<>();
        serviceData.setBo(userPager);
        serviceData.setCode("200");
        serviceData.setError("ok");
        return serviceData;
    }

    @Override
    public void run(String... args) throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("data.json");
        String json = FileCopyUtils.copyToString(new BufferedReader(new InputStreamReader(in, "UTF-8")));
        GernericServiceData<User> serviceData = mapper.readValue(json, new TypeReference<GernericServiceData<User>>() {
        });
        System.out.println(serviceData);

        JavaType type = mapper.getTypeFactory().
                constructParametricType(GernericServiceData.class, User.class);
        GernericServiceData<User> serviceData1 = mapper.readValue(json, type);
        System.out.println(serviceData1);
    }
}
