package com.httpclient;

import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;


import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.spi.SelectorProvider;

import bsh.Interpreter;
import java.util.Date;


public class App {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try {
            Interpreter i = new Interpreter();  // Construct an interpreter
            //i.set("foo", 5);                    // Set variables
            i.eval("int foo = 5;");
            i.eval("System.out.println(foo);");
        } catch(Exception e) {
            System.out.println("get exception:" + e);
        }
        
        example();
    }

    public static void example() throws URISyntaxException, IOException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(2);
        cm.setDefaultMaxPerRoute(2);


        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).build();
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(5000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        for(int i=0;i<1000;i++) {
            try {
                Thread.sleep(2000);
                ThreadTask task = new ThreadTask(restTemplate);
                Thread th = new Thread(task);
                th.start();
            } catch(Exception e) {
                System.out.println("get exception when execute new thread:" + e);
                return;
            }
        }
    }
}