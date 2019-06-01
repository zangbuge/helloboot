package com.hugmount.helloboot;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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


    @Test
    public void TestCallable() {
        ExecutorService executorService = ThreadUtil.getExecutorService();
        CallableDemo callableDemo = new CallableDemo("lhm");
        Future submit = executorService.submit(callableDemo);
        System.out.println(JSON.toJSONString(submit));
        executorService.shutdown();
        System.out.println(JSON.toJSONString(submit));
    }

    public class CallableDemo implements Callable {

        private String name;

        public CallableDemo(String name){
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         * @return computed result
         *
         * @throws Exception if unable to compute a result
         */
        @Override
        public Object call() throws Exception {
            System.out.println("使用callable开启的线程, name: " + name);
            return null;
        }
    }


}
