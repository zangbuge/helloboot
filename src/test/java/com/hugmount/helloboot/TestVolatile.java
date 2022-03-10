package com.hugmount.helloboot;

import java.util.ArrayList;
import java.util.List;

/**
 * volatile 修饰符适用于以下场景：某个属性被多个线程共享，其中有一个线程修改了此属性，其他线程可以立即得到修改后的值；实现轻量级同步
 * volatile 属性的读写操作都是无锁的，它不能替代 synchronized，因为它没有提供原子性和互斥性。因为无锁，不需要花费时间在获取锁和释放锁上，所以说它是低成本的
 * volatile 只能作用于属性，无法用于修饰方法, 我们用 volatile 修饰属性，这样编译器就不会对这个属性做指令重排序
 * volatile 两个特性, 1.禁止指令重排, 2.提供了可见性，任何一个线程对其的修改将立马对其他线程可见
 *
 * @author Li Huiming
 * @date 2022/3/10
 */
public class TestVolatile {
    // 定义一个共享变量来实现通信，它需要是volatile修饰，否则线程不能及时感知
    static volatile boolean notice = false;

    /**
     * 题目：有两个线程A、B，A线程向一个集合里面依次添加元素"abc"字符串，一共添加十次，当添加到第五次的时候，
     * 希望B线程能够收到A线程的通知，然后B线程执行相关的业务操作
     */
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        // 实现线程A
        Thread threadA = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                list.add("abc");
                System.out.println("线程A向list中添加一个元素，此时list中的元素个数为：" + list.size());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (list.size() == 5)
                    notice = true;
            }
        });
        // 实现线程B
        Thread threadB = new Thread(() -> {
            while (true) {
                if (notice) {
                    System.out.println("线程B收到通知，开始执行自己的业务...");
                    break;
                }
            }
        });
        //　需要先启动线程B
        threadB.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 再启动线程A
        threadA.start();
    }


}
