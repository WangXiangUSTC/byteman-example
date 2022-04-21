
# example.httpclientpool

## Install Byteman

Download binary and document:

```bash
wget https://downloads.jboss.org/byteman/4.0.14/byteman-download-4.0.14-bin.zip
```

Uncompress the zip file:

```bash
unzip byteman-download-4.0.14-bin.zip
```

Set the environment variable:

```bash
export BYTEMAN_HOME=`pwd`/byteman-download-4.0.14
export PATH=$PATH:${BYTEMAN_HOME}/bin
```

## Build and run the example application

Run HTTP server:

```bash
go run HTTPServer.go &
```

This HTTP server will handle HTTP requests for URL `127.0.0.1:8090/hello`.

Enter into the directory of the example:

```bash
cd com.httpclient
```

Build the Java code:

```bash
mvn package
```

You can run the application by executing the command below:

```bash
mvn exec:java -Dexec.mainClass="com.httpclient.App"
```

This application use `org.apache.http.impl.conn.PoolingHttpClientConnectionManager` to manage HTTP client pool, and send HTTP requests to `http://127.0.0.1:8090/hello`. The output looks like:

```log
app thread: Thread-1
<200 OK,hello
,{Date=[Thu, 21 Apr 2022 08:06:50 GMT], Content-Length=[6], Content-Type=[text/plain; charset=utf-8]}>
```

## Using Byteman

### Prepare Byteman script

You can see there is one file `connect.btm` under directory `rules`.

### Run Byteman

First, you need to run the example Java application:

```bash
mvn exec:java -Dexec.mainClass="com.httpclient.App"
```

Then get the PID of the process.

Attach Byteman into JVM:

```bash
bminstall.sh -b -Dorg.jboss.byteman.transform.all -Dorg.jboss.byteman.verbose ${PID}
```

Install rules:

```bash
bmsubmit.sh -l rules/connect.btm
```

The output looks like this:

```log
[WARNING] 
org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://127.0.0.1:8090/hello": BOOM; nested exception is org.apache.http.conn.ConnectionPoolTimeoutException: BOOM
    at org.springframework.web.client.RestTemplate.doExecute (RestTemplate.java:666)
    at org.springframework.web.client.RestTemplate.execute (RestTemplate.java:613)
    at org.springframework.web.client.RestTemplate.exchange (RestTemplate.java:531)
    at com.httpclient.ThreadTask.run (ThreadTask.java:17)
    at java.lang.Thread.run (Thread.java:834)
Caused by: org.apache.http.conn.ConnectionPoolTimeoutException: BOOM
    at org.jboss.byteman.rule.helper.Helper_HelperAdapter_Compiled_1.execute0 (connect.btm:7)
    at org.jboss.byteman.rule.helper.Helper_HelperAdapter_Compiled_1.execute (connect.btm)
    at org.jboss.byteman.rule.Rule.execute (Rule.java:820)
    at org.jboss.byteman.rule.Rule.execute (Rule.java:789)
    at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.leaseConnection (PoolingHttpClientConnectionManager.java)
    at org.apache.http.impl.conn.PoolingHttpClientConnectionManager$1.get (PoolingHttpClientConnectionManager.java:282)
    at org.apache.http.impl.execchain.MainClientExec.execute (MainClientExec.java:190)
    at org.apache.http.impl.execchain.ProtocolExec.execute (ProtocolExec.java:186)
    at org.apache.http.impl.execchain.RetryExec.execute (RetryExec.java:89)
    at org.apache.http.impl.execchain.RedirectExec.execute (RedirectExec.java:110)
    at org.apache.http.impl.client.InternalHttpClient.doExecute (InternalHttpClient.java:185)
    at org.apache.http.impl.client.CloseableHttpClient.execute (CloseableHttpClient.java:83)
    at org.apache.http.impl.client.CloseableHttpClient.execute (CloseableHttpClient.java:56)
    at org.springframework.http.client.HttpComponentsClientHttpRequest.executeInternal (HttpComponentsClientHttpRequest.java:89)
    at org.springframework.http.client.AbstractBufferingClientHttpRequest.executeInternal (AbstractBufferingClientHttpRequest.java:48)
    at org.springframework.http.client.AbstractClientHttpRequest.execute (AbstractClientHttpRequest.java:53)
    at org.springframework.web.client.RestTemplate.doExecute (RestTemplate.java:652)
    at org.springframework.web.client.RestTemplate.execute (RestTemplate.java:613)
    at org.springframework.web.client.RestTemplate.exchange (RestTemplate.java:531)
    at com.httpclient.ThreadTask.run (ThreadTask.java:17)
    at java.lang.Thread.run (Thread.java:834)
```

Uninstall rules:

```bash
bmsubmit.sh -u rules/connect.btm
```
