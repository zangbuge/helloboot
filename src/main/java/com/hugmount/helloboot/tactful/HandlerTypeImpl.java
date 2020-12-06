package com.hugmount.helloboot.tactful;

import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/** HandlerType 注解本质是一个接口, 是有 JDK AnnotationInvocationHandler 生成动态代理实现类
 * 需要重写本类的hashCode, equals 方法, 才可作为 hashmap 的key使用
 * @Author: Li Huiming
 * @Date: 2020/12/5
 */
@ToString
public class HandlerTypeImpl implements HandlerType {

    private String[] factor;

    public HandlerTypeImpl(String[] factor) {
        this.factor = factor;
    }

    @Override
    public String[] factor() {
        return factor;
    }

    /**
     * 添加 getType 方法, 替换将HandlerType作为map的key使用, 避免重写hashcode方法
     * @param handlerType
     * @return
     */
    public static String getType(HandlerType handlerType) {
        String[] factor = handlerType.factor();
        List<String> strings = Arrays.asList(factor);
        return strings.toString();
    }

    public String getType() {
        return getType(this);
    }

    /**
     * 实现注解接口, 必须重写该方法
     * @return
     */
    @Override
    public Class<? extends Annotation> annotationType() {
        return HandlerType.class;
    }

}
