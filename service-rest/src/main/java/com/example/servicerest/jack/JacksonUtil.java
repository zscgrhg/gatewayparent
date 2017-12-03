package com.example.servicerest.jack;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JacksonUtil<T1, T2 extends GernericServiceData<T1>> {
    @Autowired
    ObjectMapper mapper;

    public class Builder<T> {

    }
}
