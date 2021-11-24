# mysqldemo

`mysqldemo` is a demo used to query SQL to MySQL, and show how to inject fault to MySQL Client by Byteman with helper [SQLHelper](https://github.com/WangXiangUSTC/byteman-helper/tree/main/SQLHelper).

**Note**: `mysqldemo` will query SQL to MySQL with `localhost:3306/test`, and default username and password is `root` and `123456`.

## Build

```bash
mvn -X package -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```

## Run

```bash
mvn exec:java -Dexec.mainClass="com.mysqldemo.App"
```

Then you can visit `mysqldemo`'s endpoint `query` to query SQL. For example:

```bash
curl -X GET "http://127.0.0.1:8001/query?sql=SELECT%20*%20FROM%20test.websites"
```

The result look like:

```bash
id, name, url, 
1, Chaos Mesh, https://chaos-mesh.org,
```

## Inject fault by Byteman

### Install agent

```bash
bminstall.sh -b -Dorg.jboss.byteman.transform.all -Dorg.jboss.byteman.verbose -p 9288 {pid}
```

The `pid` is `mysqldemo`'s pid.

### Install jar files

1.Install SQLHelper jar file

```bash
bmsubmit.sh -p 9288 -b /PATH-TO-SQLHelper/SQLHelper-1.0.jar
```

You can build SQLHelper-1.0.jar according to the [document](https://github.com/WangXiangUSTC/byteman-helper#build).

2.Install jsqlparser jar file

```bash
bmsubmit.sh -p 9288 -b /PATH-TO-JSQLPARSER/jsqlparser-4.2.jar
```

[jsqlparser](https://github.com/JSQLParser/JSqlParser) is used to parse SQL.

### Submit rule file

Prepare a rule file `rule.btm`, for example:

```txt
RULE mysql test
CLASS com.mysql.cj.jdbc.StatementImpl
METHOD executeQuery
HELPER org.chaos_mesh.byteman.helper.SQLHelper
AT ENTRY
BIND
     flag:boolean=matchDBTable($1, "test", "websites");
IF flag
DO
        throw new java.sql.SQLException("BOOM");
ENDRULE
```

SQLs match database `test` and table `websites` will get exception when execute query.

Then execute command:

```bash
bmsubmit.sh -p 9288 -l ./rule.btm
```

### Query

```bash
curl -X GET "http://127.0.0.1:8001/query?sql=SELECT%20*%20FROM%20test.websites"
```

This time will not query success, you will get a exception:

```bash
java.sql.SQLException: BOOM
        at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
        at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:64)
        at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
        at java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:500)
        at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:481)
        at org.jboss.byteman.rule.expression.ThrowExpression.interpret(ThrowExpression.java:230)
        at org.jboss.byteman.rule.Action.interpret(Action.java:144)
        at org.chaos_mesh.byteman.helper.MySQLHelper_HelperAdapter_Interpreted_1.fire(mysql3.btm)
        at org.chaos_mesh.byteman.helper.MySQLHelper_HelperAdapter_Interpreted_1.execute0(mysql3.btm)
        at org.chaos_mesh.byteman.helper.MySQLHelper_HelperAdapter_Interpreted_1.execute(mysql3.btm)
        at org.jboss.byteman.rule.Rule.execute(Rule.java:810)
        at org.jboss.byteman.rule.Rule.execute(Rule.java:779)
        at com.mysql.cj.jdbc.StatementImpl.executeQuery(StatementImpl.java)
        at com.mysqldemo.App$TestHandler.handle(App.java:58)
        at jdk.httpserver/com.sun.net.httpserver.Filter$Chain.doFilter(Filter.java:77)
        at jdk.httpserver/sun.net.httpserver.AuthFilter.doFilter(AuthFilter.java:82)
        at jdk.httpserver/com.sun.net.httpserver.Filter$Chain.doFilter(Filter.java:80)
        at jdk.httpserver/sun.net.httpserver.ServerImpl$Exchange$LinkHandler.handle(ServerImpl.java:692)
        at jdk.httpserver/com.sun.net.httpserver.Filter$Chain.doFilter(Filter.java:77)
        at jdk.httpserver/sun.net.httpserver.ServerImpl$Exchange.run(ServerImpl.java:664)
        at jdk.httpserver/sun.net.httpserver.ServerImpl$DefaultExecutor.execute(ServerImpl.java:159)
        at jdk.httpserver/sun.net.httpserver.ServerImpl$Dispatcher.handle(ServerImpl.java:442)
        at jdk.httpserver/sun.net.httpserver.ServerImpl$Dispatcher.run(ServerImpl.java:408)
        at java.base/java.lang.Thread.run(Thread.java:832)
```
