package com.httpclient;

import java.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

public class ThreadTask implements Runnable{
    RestTemplate restTemplate;

    public ThreadTask(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void run() {
        System.out.println("app thread: " + Thread.currentThread().getName());
        ResponseEntity<String> exchange = restTemplate.exchange("http://127.0.0.1:8090/hello", HttpMethod.GET, null,  String.class);
        System.out.println(exchange);
    }
}
