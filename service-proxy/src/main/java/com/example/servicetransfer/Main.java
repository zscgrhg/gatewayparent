package com.example.servicetransfer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Main {


    public static void main(String[] args) {


        String url = "http://127.0.0.1/gw/service/rest/1/hello";

        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test(url);
                }
            }).start();
        }

    }

    public static long test(String url) {
        final RestTemplate restTemplate = new RestTemplate();
        long start = System.nanoTime();
        long len = 0;
        for (int i = 0; i < 10000; i++) {
            ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(
                    url, byte[].class);
            byte[] body = responseEntity.getBody();
            len += body.length;
        }
        System.out.println("estimatedTime:" + (System.nanoTime() - start));
        return len;
    }
}
