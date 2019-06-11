package com.hugmount.helloboot.queue;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Li Huiming
 * @Date: 2019/6/11
 */
public class TestQueue {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        //转为线程安全的集合,详情查看源码
        List<String> list1 = Collections.synchronizedList(list);
        //该map是不可被改变的
        Map<Object, Object> objectObjectMap = Collections.unmodifiableMap(new HashMap<>());
        // 非线程安全
        final Queue<String> queue = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            // 把元素放在集合队列的末尾
            queue.offer(String.valueOf(i));
        }
        for (int i = 0; i < 10; i++) {
            // 取队列的第一个元素消费并移除, 返回当前元素
            String remove = queue.remove();
            System.out.print("已消费 " + remove);
            // 获取队列的第一个元素, 并没有消费移除
            String peek = queue.peek();
            System.out.println(" 当前队列第一个元素为: " + peek);
        }

        // 线程安全
        final BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < 10; i++) {
            try {
                blockingQueue.put(String.valueOf(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 10; i++) {
            try {
                //消费队列
                String take = blockingQueue.take();
                System.out.println("blockingQueue消费了 " + take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }





    }
}
/*
final关键字可以修饰类，方法，成员变量，final修饰的类不能被继承，final修饰的方法不能被重写，
final修饰的成员变量必须初始化值，如果这个成员变量是基本数据类型，表示这个变量的值是不可改变的，
如果说这个成员变量是引用类型，则表示这个引用的地址值是不能改变的，但是这个引用所指向的对象里面的内容还是可以改变的
* */