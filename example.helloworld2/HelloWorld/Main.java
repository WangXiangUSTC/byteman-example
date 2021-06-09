package HelloWorld;

import java.util.*;
import java.time.*;

public class Main {
    public static void main(String []args) {
        ArrayList<String> data = new ArrayList<String>();

        for(int x = 0; x < 1000; x = x+1) {
            try {
                Thread.sleep(1000);
                Main.sayhello(x);
            } catch (Exception e) {
                System.out.println("Got an exception!" + e);
            }
        }
    }

    public static void sayhello(int num) throws Exception {
        try {
            Info info = getInfo();
            System.out.println(info.GetInfo());
        } catch (Exception e) { 
            throw e ;
        }
    }

    public static Info getInfo() {
        Info info = new Info(LocalDateTime.now(), "Hello World");
        return info;
    }
}
