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

        System.out.println("获取该类及基类所有public方法");
        Method[] methods = orderLogClass.getMethods();
        System.out.println(JSON.toJSONString(methods));

        Method[] declaredMethods = orderLogClass.getDeclaredMethods();
        System.out.println("获取该类本身所有方法(私有 公共 保护)");
        System.out.println(JSON.toJSONString(declaredMethods));

        System.out.println("该类及父类所有public属性");
        Field[] fields = orderLogClass.getFields();
        System.out.println(JSON.toJSONString(fields));

        System.out.println("获取该类本身所有属性成员(包含private)");
        Field[] declaredFields = orderLogClass.getDeclaredFields();
        System.out.println(JSON.toJSONString(declaredFields));

        System.out.println("获取该类本身某个属性的值");
        Field declaredField = orderLogClass.getDeclaredField("date");
        OrderLog orderLog = orderLogClass.newInstance();
        System.out.println("date: " + declaredField.get(orderLog));


    }
}
