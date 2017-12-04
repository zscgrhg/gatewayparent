package com.example.servicerest.jack;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GernericServiceData<D> extends ServiceData implements GernericStruct<D> {
    private Pager<D> payload;

    @Override
    @JsonIgnore
    public Object getBo() {
        return super.getBo();
    }


    @JsonProperty("bo")
    public Pager<D> getPayload() {
        return payload;
    }


    public void setPayload(Pager<D> payload) {
        this.payload = payload;
    }
}
