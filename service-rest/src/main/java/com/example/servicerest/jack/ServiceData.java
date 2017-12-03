package com.example.servicerest.jack;

public class ServiceData<D> {
    private String code;
    private String error;
    private Pager<D> payload;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Pager<D> getPayload() {
        return payload;
    }

    public void setPayload(Pager<D> payload) {
        this.payload = payload;
    }
}
