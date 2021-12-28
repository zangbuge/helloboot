package com.hugmount.helloboot.util;

import java.lang.reflect.*;

/**
 * @author Li Huiming
 * @date 2021/12/23
 */
public class ClassUtil {

    public static Class getImplIfaceActualType(Class clazz, int ifaceIndex, int actualTypeIndex) {
        // 获取该类实现的接口
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        ParameterizedType genericInterface = (ParameterizedType) genericInterfaces[ifaceIndex];
        // 接口的泛型类型
        Type[] actualTypeArguments = genericInterface.getActualTypeArguments();
        Class actualClazz = (Class) actualTypeArguments[actualTypeIndex];
        return actualClazz;
    }

    /**
     * 获取一个类父类的泛型
     *
     * @param clazz
     * @param actualTypeIndex
     * @return
     */
    public static Class getSupperClassActualType(Class clazz, int actualTypeIndex) {
        // 获取该类的父类
        Type superClass = clazz.getGenericSuperclass();
        ParameterizedType genericInterface = (ParameterizedType) superClass;
        // 该类泛型的类型
        Type[] actualTypeArguments = genericInterface.getActualTypeArguments();
        Class actualClazz = (Class) actualTypeArguments[actualTypeIndex];
        return actualClazz;
    }

    public static void setFieldValue(Object obj, String fieldName, String fieldValue) {
        try {
            Field field = obj.getClass().getField(fieldName);
            field.set(obj, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException("设置字段参数异常", e);
        }
    }


    public static void getMethod(Object obj, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            // 值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查
            method.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("反射获取方法对象异常", e);
        }
    }

    public static Object invokeMethod(Object obj, Method method, Object... parameters) {
        try {
            return method.invoke(obj, method, parameters);
        } catch (Exception e) {
            throw new RuntimeException("反射执行方法异常", e);
        }
    }

}
