package ThreadPool;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;


public class ThreadPool {
    public static ThreadPoolExecutor pool;
    public static void main( String[] args )
    {
        //maximumPoolSize设置为2 ，拒绝策略为AbortPolic策略，直接抛出异常
        pool = new ThreadPoolExecutor(1, 10, 10, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        for(int i=0;i<100;i++) {
            try {
                Thread.sleep(1000);
                pool.execute(new ThreadTask());
            } catch(Exception e) {
                System.out.println("get exception when execute new thread:" + e);
            }
        }

        System.out.println("main thread: " + Thread.currentThread().getName());
        return;
    }
}
