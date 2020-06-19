package com.mk.pbs.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: IO操作线程池
 * @author: zyj
 * @create: 2020-03-20 11:54
 **/
public class ThreadPoolHelper {

    private static int process = Runtime.getRuntime().availableProcessors();

    private static int corePoolSize = Math.max(2, Math.min(process - 1, 4));

    private static long keepAliveTime = 30;

    private static int maximumpoolsize = process * 2 + 1;

    private static BlockingQueue workQueue = new LinkedBlockingQueue<>();

    private static class ThreadPoolHelperHolder {
        private static ThreadPoolHelper instance = new ThreadPoolHelper();
    }

    public static ThreadPoolHelper getInstance() {
        return ThreadPoolHelperHolder.instance;
    }


    private static ThreadPoolExecutor executor;

    public  void execute(Runnable runnable){
        if (runnable == null) {
            return;
        }

        if (executor == null) {
            executor =   new ThreadPoolExecutor(corePoolSize, maximumpoolsize, keepAliveTime,
                TimeUnit.SECONDS, workQueue);
        }
        executor.execute(runnable);
    }




}
