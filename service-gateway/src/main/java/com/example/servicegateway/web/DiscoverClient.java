package com.example.servicegateway.web;

public class DiscoverClient {
    public static String getRestURI(String path){
        return "http://127.0.0.1:8080/"+path;
    }
}
