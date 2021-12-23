package com.hugmount.helloboot.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

}
