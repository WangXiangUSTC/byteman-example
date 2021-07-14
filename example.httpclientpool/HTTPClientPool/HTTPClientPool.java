package HTTPClientPool;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpHost;


public class HTTPClientPool {

    public static void main(String []args) { 
        
        RequestConfig requestConfig = RequestConfig //请求设置
                    .custom()
                    .setConnectionRequestTimeout(ConnectionRequestTimeout) //从连接池获取连接超时时间
                    .setConnectTimeout(ConnectionTimeout) //连接超时时间
                    .setSocketTimeout(SocketTimeout).build(); //套接字超时时间
 
            HttpClientBuilder builder = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setMaxConnTotal(MaxConnTotal); //设置最大连接数
 
            httpClient = builder.build();
        
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        // Increase max connections for localhost:80 to 50
        HttpHost localhost = new HttpHost("locahost", 80);
        cm.setMaxPerRoute(new HttpRoute(localhost), 50);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();
    }
}