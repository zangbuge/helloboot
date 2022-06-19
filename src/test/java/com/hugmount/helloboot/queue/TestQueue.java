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
//        1)add(anObject):把anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则招聘异常
//        2)offer(anObject):表示如果可能的话,将anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则返回false.
//        3)put(anObject):把anObject加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程被阻断直到BlockingQueue里面有空间再继续.
//        4)poll(time):取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null
//        5)take():取走BlockingQueue里排在首位的对象,若BlockingQueue为空,阻断进入等待状态直到Blocking有新的对象被加入为止
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

        // 四个实现类
//        1)ArrayBlockingQueue:规定大小的BlockingQueue,其构造函数必须带一个int参数来指明其大小.其所含的对象是以FIFO(先入先出)顺序排序的.
//        2)LinkedBlockingQueue:大小不定的BlockingQueue,若其构造函数带一个规定大小的参数,生成的BlockingQueue有大小限制,若不带大小参数,所生成的BlockingQueue的大小由Integer.MAX_VALUE来决定.其所含的对象是以FIFO(先入先出)顺序排序的
//        3)PriorityBlockingQueue:类似于LinkedBlockQueue,但其所含对象的排序不是FIFO,而是依据对象的自然排序顺序或者是构造函数的Comparator决定的顺序.
//        4)SynchronousQueue:特殊的BlockingQueue,对其的操作必须是放和取交替完成的.


    }
}
/*
final关键字可以修饰类，方法，成员变量，final修饰的类不能被继承，final修饰的方法不能被重写，
final修饰的成员变量必须初始化值，如果这个成员变量是基本数据类型，表示这个变量的值是不可改变的，
如果说这个成员变量是引用类型，则表示这个引用的地址值是不能改变的，但是这个引用所指向的对象里面的内容还是可以改变的
* */