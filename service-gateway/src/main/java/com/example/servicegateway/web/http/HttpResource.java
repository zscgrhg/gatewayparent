package com.example.servicegateway.web.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface HttpResource extends Closeable{
    OutputStream   getRequestStream();
    InputStream getResponseStream() throws IOException;
     Map<String,String> getResponseHeaders();
    public int getResponseCode() throws IOException;
}
