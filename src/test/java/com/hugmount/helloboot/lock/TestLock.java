package com.hugmount.helloboot.lock;

import com.hugmount.helloboot.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Li Huiming
 * @Date: 2019/6/4
 */
public class TestLock extends Thread{

    /**
     * lock的使用
     * @param args
     */
    public static void main (String [] args) {
        TicketThread ticketThread = new TicketThread();
        Thread thread1 = new Thread(ticketThread ,"襄阳");
        Thread thread2 = new Thread(ticketThread ,"武汉");
        Thread thread3 = new Thread(ticketThread ,"北京");

        thread1.start();
        thread2.start();
        thread3.start();
    }

    public static class TicketThread implements Runnable {
        private int tickets = 1000;
        private Lock lock = new ReentrantLock();

        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    System.out.print("已加锁  ");
                    if (tickets > 0) {
//                        Thread.sleep(100);
                        System.out.print(Thread.currentThread().getName() + "正在出售第" + (tickets--) + "张票  ");
                    }
                    else {
                        System.out.print("执行完成  ");
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //try 中如果包含return ,finally 执行后再return
                } finally {
                    System.out.println("已释放锁, 剩余" + tickets + "张票");
                    lock.unlock();
                }
            }

        }
    }

    //-------------------------------------------------------------------

    /**
     * 容易发生死锁的线程
     */
    @Test
    public void dielockDemo () {
        CallTask callTaska = new CallTask(true);
        CallTask callTaskb = new CallTask(false);
        ExecutorService executorService = ThreadUtil.getExecutorService();
        executorService.submit(callTaska);
        executorService.submit(callTaskb);
    }

    static class CallTask implements Callable {
        // 创建两把锁对象
        public static final Object objA = new Object();
        public static final Object objB = new Object();

        //是否死锁
        private boolean isDieLock;

        public CallTask(boolean isDieLock) {
            this.isDieLock = isDieLock;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        public Object call() throws Exception {

            if (isDieLock) {
                synchronized (objA) {
                    System.out.println("获取了锁: objA");
                    synchronized (objB) {
                        System.out.println("获取了锁: objB");
                    }
                }
            }
            else {
                synchronized (objB) {
                    System.out.println("获取了锁: objB");
                    synchronized (objA) {
                        System.out.println("获取了锁: objA");
                    }
                }
            }

            return null;
        }
    }
}
