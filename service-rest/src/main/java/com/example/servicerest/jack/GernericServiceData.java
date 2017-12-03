package com.example.servicerest.jack;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class GernericServiceData<D> extends ServiceData {
    private Pager<D> payload;

    @Override
    @JsonIgnore
    public Object getBo() {
        return super.getBo();
    }


    @JsonProperty("bo")
    @JsonDeserialize(using = ItemDeserializer.class)
    public Pager<D> getPayload() {
        return payload;
    }


    public void setPayload(Pager<D> payload) {
        this.payload = payload;
    }
}
