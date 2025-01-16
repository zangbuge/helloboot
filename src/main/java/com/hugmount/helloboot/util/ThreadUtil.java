package com.hugmount.helloboot.util;

import java.util.concurrent.*;

/**
 * 线程工具类
 *
 * @Author: Li Huiming
 * @Date: 2019/5/14
 */
public class ThreadUtil {

    private ThreadUtil() {
    }

    public static synchronized ExecutorService getExecutorService() {
        // 默认拒绝策略: 直接抛异常
        return new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
    }

    public static void execute(Runnable runnable) {
        if (null == runnable) {
            return;
        }
        CompletableFuture.runAsync(runnable);
    }

    public static void main(String[] args) {
        // 创建一个固定大小的线程池
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
        // 创建一个Runnable任务
        Runnable task = () -> System.out.println("Task executed at: " + System.nanoTime());
        // 延迟1秒后执行任务，然后每3秒执行一次
        executorService.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS);
        // 如果需要在一定时间后关闭线程池，可以执行以下代码
        // executorService.shutdown();
    }

}

/**
 * 1.newSingleThreadExecutor
 * <p>
 * 创建一个单线程的线程池。这个线程池只有一个线程在工作，也就是相当于单线程串行执行所有任务。
 * 如果这个唯一的线程因为异常结束，那么会有一个新的线程来替代它。此线程池保证所有任务的执行顺序按照任务的提交顺序执行。
 * <p>
 * 2.newFixedThreadPool
 * <p>
 * 创建固定大小的线程池。每次提交一个任务就创建一个线程，直到线程达到线程池的最大大小。
 * 线程池的大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。
 * <p>
 * 3.newCachedThreadPool
 * <p>
 * 创建一个可缓存的线程池。如果线程池的大小超过了处理任务所需要的线程，
 * 那么就会回收部分空闲（60秒不执行任务）的线程，当任务数增加时，此线程池又可以智能的添加新线程来处理任务。
 * 此线程池不会对线程池大小做限制，线程池大小完全依赖于操作系统（或者说JVM）能够创建的最大线程大小。
 * <p>
 * 4.newScheduledThreadPool
 * <p>
 * 创建一个大小无限的线程池。此线程池支持定时以及周期性执行任务的需求。
 * 可实现定时器功能.
 */