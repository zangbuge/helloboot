package com.hugmount.helloboot.annotation;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author: Li Huiming
 * @Date: 2019/6/14
 */
public class Test {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Class<OrderLog> orderLogClass = OrderLog.class;

        //验证
        if (orderLogClass.isAnnotationPresent(LogInfo.class)) {
            LogInfo annotation = orderLogClass.getAnnotation(LogInfo.class);
            System.out.println("path = " + annotation.path());
        }

        Method[] methods = orderLogClass.getMethods();
        Field[] fields = orderLogClass.getFields();
        Field declaredField = orderLogClass.getDeclaredField("date");
        OrderLog orderLog = orderLogClass.newInstance();
        System.out.println("date的值: " + declaredField.get(orderLog));
        
        System.out.println("该类所有public方法");
        System.out.println(JSON.toJSONString(methods, true));
        System.out.println("该类所有public属性");
        System.out.println(JSON.toJSONString(fields ,true));


    }
}
