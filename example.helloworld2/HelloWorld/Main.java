package HelloWorld;

import java.util.*;
import java.time.LocalDateTime;

public class Main {
    public static void main(String []args) {
        ArrayList<String> data = new ArrayList<String>();

        for(int x = 0; x < 1000; x = x+1) {
            try {
                Thread.sleep(1000);
                Info info = Main.getInfo(x);
                System.out.println(info.Str());
            } catch (Exception e) {
                System.out.println("Got an exception!" + e);
            }
        }
    }

    public static Info getInfo(int num) throws Exception {
        try {
            LocalDateTime a = LocalDateTime.of(2017, 2, 13, 15, 56);    
            Info info = new Info(num, LocalDateTime.now(), "Hello World");
            return info;
        } catch (Exception e) {
            throw e;
        }
    }

    public static int getnum(int num) {
        return num;
    }
}
