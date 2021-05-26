package com.httpclient;

import java.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

public class ThreadTask implements Runnable{
    RestTemplate restTemplate;

    public ThreadTask(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void run() {
        System.out.println("app thread: " + Thread.currentThread().getName());
        ResponseEntity<String> response = restTemplate.getForEntity("http://127.0.0.1:8090/hello", String.class);
        System.out.println(response);
        /*
        if (interval > 0) {
            try {
                Thread.sleep(interval);
            } catch(Exception e) {
                System.out.println("get exception when execute new thread:" + e);
                return;        
            }
        }
        */
    }
}
