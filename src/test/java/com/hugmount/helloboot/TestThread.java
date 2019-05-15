package com.hugmount.helloboot;

import com.hugmount.helloboot.util.ThreadUtil;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/14
 */
public class TestThread {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            final String index = "" + i;
            //lambda参数必须为final修饰
            ThreadUtil.execute(() -> {
                System.out.println("线程A" + index);
            });
            ThreadUtil.execute(() -> {
                System.out.println("线程B" + index);
            });
        }
    }
}
