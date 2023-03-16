package com.hugmount.helloboot;

import com.hugmount.helloboot.test.pojo.User;
import com.hugmount.helloboot.util.LambdaFun;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author lhm
 * @date 2023/3/15
 */
public class SerializedLambdaTest {
    public static void main(String[] args) throws Exception {
        test(User::getUsername);
    }

    private static <T> void test(LambdaFun<T, Object> consumer) throws Exception {
        // 直接调用writeReplace
        Method writeReplace = consumer.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        Object sl = writeReplace.invoke(consumer);
        SerializedLambda serializedLambda = (SerializedLambda) sl;
        System.out.println("serializedLambda数据为：" + serializedLambda);
        System.out.println("传入的方法名为:" + serializedLambda.getImplMethodName());
    }
}
