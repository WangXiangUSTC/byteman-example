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
    // for MySQL 8.0 below
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/test";
 
    // for MySQL 8.0 above
    //static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    //static final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
 
    // user and password 
    static final String USER = "root";
    static final String PASS = "123456";

    public static void main( String[] args) throws Exception
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/query", new TestHandler());
        server.start();
    }

    static class TestHandler implements HttpHandler{
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
                System.out.println("sql: " + sql);

                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
        
                ResultSetMetaData metadata = rs.getMetaData();
                int columnCount = metadata.getColumnCount();    
                for (int i = 1; i <= columnCount; i++) {
                    response += metadata.getColumnName(i) + ", ";
                }
                response += "\n";
                while(rs.next()){   
                    for (int i = 1; i <= columnCount; i++) {
                        response += rs.getString(i) + ", ";          
                    }
                    response += "\n";
                }
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
