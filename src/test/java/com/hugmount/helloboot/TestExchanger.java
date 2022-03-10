package com.hugmount.helloboot;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 每个线程调用exchage方法到达各自的同步点，当且仅当两个线程都达到同步点的时候，才可以交换信息，
 * 否则先到达同步点的线程必须等待
 *
 * @author Li Huiming
 * @date 2022/3/10
 */
public class TestExchanger {

    public static void main(String[] args) {
        // 只适用于2个线程之间的信息交换, 当超过2个线程调用同一个Exchanger时，得到的结果是不可预料的
        final Exchanger<String> exchanger = new Exchanger<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> {
            System.out.println(Thread.currentThread().getName() + "我有白粉，准备交换钱");
            try {
                Thread.sleep(2000);
                String res = exchanger.exchange("白粉");
                System.out.println(Thread.currentThread().getName() + "换回来的为:" + res);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.execute(() -> {
            System.out.println(Thread.currentThread().getName() + "我有钱，准备交换白粉");
            try {
                Thread.sleep(2000);
                String res = exchanger.exchange("人民币");
                System.out.println(Thread.currentThread().getName() + "换回来的为:" + res);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
    }
}
