package com.mysqldemo;

import java.sql.*;
import java.util.*;
import java.io.UnsupportedEncodingException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class App 
{
    static String jdbcDriver;
    static String dsn;
    
    // MySQL user and password
    static String user;
    static String password;

    public static void main( String[] args) throws Exception
    {
        System.out.println("Server start!");
        App.setConfig();
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/query", new QueryHandler());
        server.start();
    }

    // setConfig set the config from environment variables, or use default value.
    public static void setConfig() {
        String mysqlDSN = System.getenv("MYSQL_DSN");
        String mysqlUser = System.getenv("MYSQL_USER");
        String mysqlPassword = System.getenv("MYSQL_PASSWORD");
        String mysqlConnectorVersion = System.getenv("MYSQL_CONNECTOR_VERSION");

        if (mysqlDSN == null || mysqlDSN.equals("")) {
            App.dsn = "jdbc:mysql://localhost:3306/test";
        } else {
            App.dsn = mysqlDSN;
        }

        if (mysqlUser == null || mysqlUser.equals("")) {
            App.user = "root";
        } else {
            App.user = mysqlUser;
        }

        if (mysqlPassword != null && mysqlPassword.length() != 0) {
            App.password = mysqlPassword;
        }

        if (mysqlConnectorVersion == null || mysqlConnectorVersion.equals("")) {
            // default mysql connector version is 8
            App.jdbcDriver = "com.mysql.cj.jdbc.Driver"; 
        } else {
            if (mysqlConnectorVersion.equals("8")) {
                App.jdbcDriver = "com.mysql.cj.jdbc.Driver";
            } else if (mysqlConnectorVersion.equals("5")) {
                App.jdbcDriver = "com.mysql.jdbc.Driver";
            }
        }  
    }

    // QueryHandler will handle the HTTP request, get the query SQL and then execute it, 
    // finially write the result to HTTP response
    static class QueryHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Connection conn = null;
            Statement stmt = null;
            String response = "";
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            try{
                String queryString = exchange.getRequestURI().getQuery();
                Map<String,String> queryStringInfo = formData2Dic(queryString);
                String sql = queryStringInfo.get("sql");
                System.out.println("Query sql: " + sql);

                Class.forName(App.jdbcDriver);
                if (password == null || password.length() == 0 ) {
                    conn = DriverManager.getConnection(dsn, user, null);
                } else {
                    conn = DriverManager.getConnection(dsn, user, password);
                }
                conn = DriverManager.getConnection(dsn, user, password);
                stmt = conn.createStatement();
                response += App.querySQL(stmt, sql) + "\n";
            } catch(SQLException se) {
                se.printStackTrace(pw);
                response += sw.toString() + "\n";
            } catch(Exception e) {
                e.printStackTrace(pw);
                response += sw.toString() + "\n";
            } finally {
                try {
                    if(stmt!=null) stmt.close();
                } catch(SQLException se2) {
                }
                try {
                    if(conn!=null) conn.close();
                } catch(SQLException se) {
                    se.printStackTrace();
                }
            }
            System.out.println("Finish query!");
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // querySQL executes the sql, and return the result
    public static String querySQL(Statement stmt, String sql) throws SQLException {
        String result = "";
        long start = System.currentTimeMillis();
        ResultSet rs = stmt.executeQuery(sql);

        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            result += metadata.getColumnName(i);
            if (i != columnCount) {
                result += ", ";
            }
        }
        result += "\n";
        while(rs.next()){
            for (int i = 1; i <= columnCount; i++) {
                result += rs.getString(i);
                if (i != columnCount) {
                    result += ", ";
                }
            }
            result += "\n";
        }
        long end = System.currentTimeMillis( );
        long diff = end - start;
        result += "Elapsed time: " + Long.toString(diff) + "(ms)";

        return result;
    }

    public static Map<String,String> formData2Dic(String formData ) {
        Map<String,String> result = new HashMap<>();
        if(formData== null || formData.trim().length() == 0) {
            return result;
        }
        final String[] items = formData.split("&");
        Arrays.stream(items).forEach(item ->{
            final String[] keyAndVal = item.split("=");
            if( keyAndVal.length == 2) {
                try{
                    final String key = URLDecoder.decode(keyAndVal[0],"utf8");
                    final String val = URLDecoder.decode(keyAndVal[1],"utf8");
                    result.put(key,val);
                }catch (UnsupportedEncodingException e) {}
            }
        });
        return result;
    }
}
