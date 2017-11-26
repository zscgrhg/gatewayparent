package com.example.servicegateway.web;

import java.io.ByteArrayOutputStream;

public class ByteArrStream extends ByteArrayOutputStream {
    private int size=0;
    private final int OVER_LIMIT=1024*1024;//1M
    @Override
    public synchronized void write(int b) {
        checkOverLimit();
        super.write(b);
        size++;
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        checkOverLimit();
        super.write(b, off, len);
        size+=len;
    }

    private void checkOverLimit(){
        if(size>OVER_LIMIT){
            throw new RuntimeException("OVER_LIMIT");
        }
    }
}
