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

/**
 * describe
 *
 * @Author: soul
 * @Date: 2016/12/5
 * @since: JDK 1.8
 * @Version: v1.0
 */

public class App {
    public static void main(String[] args) throws IOException, URISyntaxException {
        example();
    }

    public static void example() throws URISyntaxException, IOException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(1);
        cm.setDefaultMaxPerRoute(1);

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).build();

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(5000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        for(int i=0;i<1000;i++) {
            try {
                ThreadTask task = new ThreadTask(restTemplate);
                task.run();
            } catch(Exception e) {
                System.out.println("get exception when execute new thread:" + e);
                return;
            }
        }

        ResponseEntity<String> response = restTemplate.getForEntity("http://127.0.0.1:8090/hello", String.class);
        System.out.println(response);
/*
        //创建httpclient实例，采用默认的参数配置
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //通过URIBuilder类创建URI
        URI uri = new URIBuilder().setScheme("http")
                    .setHost("www.baidu.com")
                    .build();

        HttpGet get = new HttpGet(uri) ;   //使用Get方法提交

        //请求的参数配置，分别设置连接池获取连接的超时时间，连接上服务器的时间，服务器返回数据的时间
        RequestConfig config = RequestConfig.custom()
                                            .setConnectionRequestTimeout(3000)
                                            .setConnectTimeout(3000)
                                            .setSocketTimeout(3000)
                                            .build();
        //配置信息添加到Get请求中
        get.setConfig(config);
        //通过httpclient的execute提交 请求 ，并用CloseableHttpResponse接受返回信息
        CloseableHttpResponse response = httpClient.execute(get);
        //服务器返回的状态
        int statusCode = response.getStatusLine().getStatusCode() ;
        //判断返回的状态码是否是200 ，200 代表服务器响应成功，并成功返回信息
        if(statusCode == HttpStatus.SC_OK){
            //EntityUtils 获取返回的信息。官方不建议使用使用此类来处理信息
            System.out.println("Demo.example -------->" + EntityUtils.toString(response.getEntity() , Consts.UTF_8));
        }else {
            System.out.println("Demo.example -------->" + "获取信息失败");
        }
        */
    }
}