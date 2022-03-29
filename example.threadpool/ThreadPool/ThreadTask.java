package ThreadPool;

import java.util.*;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.SynchronousQueue;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.Executors;

public class ThreadTask implements Runnable{
    int interval;
    public ThreadTask() {
        
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    public void run() {
        System.out.println("app thread: " + Thread.currentThread().getName());
         if (interval > 0) {
            try {
                Thread.sleep(interval);
            } catch(Exception e) {
                System.out.println("get exception when execute new thread:" + e);
                return;        
            }
        }
    }
}
