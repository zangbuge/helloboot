package com.hugmount.helloboot.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 线程工具类
 * @Author: Li Huiming
 * @Date: 2019/5/14
 */
public class ThreadUtil {
    //newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    //获取单例线程次
    public static synchronized ExecutorService getExecutorService(){
        return executorService;
    }

    public static void execute(Runnable runnable) {
        if (null == runnable){
            return;
        }
        getExecutorService().execute(runnable);
    }

}
