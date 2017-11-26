package com.example.servicegateway.web.http;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpResourceImpl implements HttpResource {

    final URL resource;
    final HttpURLConnection connection;
    final OutputStream requestStream;


    public HttpResourceImpl(URL resource, Map<String, String> requestHeaders,boolean isPost) throws IOException {
        this.resource = resource;
        this.connection = (HttpURLConnection)resource.openConnection();
        if(isPost){
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
        }
        requestHeaders.forEach((k,v)->{
            connection.addRequestProperty(k,v);
        });
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        if(isPost){
            requestStream=connection.getOutputStream();
        }else {
            requestStream=null;
        }

    }

    public Map<String,String> getResponseHeaders(){
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        Map<String,String> headers=new HashMap<>();
        headerFields.keySet().forEach(k->{
            headers.put(k,connection.getHeaderField(k));
        });
        return headers;
    }

    public int getResponseCode() throws IOException {
       return connection.getResponseCode();
    }

    @Override
    public OutputStream getRequestStream() {
        return requestStream;
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        return connection.getInputStream();
    }

    @Override
    public void close() throws IOException {
        Closeable[] closeables = new Closeable[]{getRequestStream(), getResponseStream()};
        for (Closeable closeable : closeables) {
            try {
                if(closeable!=null){
                    closeable.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
