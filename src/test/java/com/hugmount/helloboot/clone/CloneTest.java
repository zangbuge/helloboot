package com.hugmount.helloboot.clone;

import cn.hutool.json.JSONUtil;
import com.hugmount.helloboot.test.pojo.User;
import org.junit.Test;

/**
 * 浅克隆: 如果原型对象的成员变量是值类型，将复制一份给克隆对象；如果是引用类型，则将引用对象的地址复制一份给克隆对象
 * 深克隆: 无论原型对象的成员变量是值类型还是引用类型，都将复制一份给克隆对象
 * 深克隆实现的三种方式
 * 1. 本类测试的方法, 实现Cloneable接口
 * 2. java序列化
 * 3. 使用json工具
 *
 * @author lhm
 * @date 2024/7/9
 */
public class CloneTest {

    /**
     * 浅克隆
     *
     * @param args
     * @throws CloneNotSupportedException
     */
    public static void main(String[] args) throws CloneNotSupportedException {
        SimpleClone simpleClone = new SimpleClone();
        simpleClone.setAge(1);
        User user = new User();
        user.setUsername("lhm");
        simpleClone.setUser(user);
        SimpleClone clone = (SimpleClone) simpleClone.clone();
        System.out.println("是否为同一个对象" + (clone == simpleClone));
        System.out.println("clone: " + JSONUtil.toJsonStr(clone));
        simpleClone.setAge(2);
        user.setUsername("lhm2");
        System.out.println("原始值修改: " + JSONUtil.toJsonStr(simpleClone));
        System.out.println("修改后clone: " + JSONUtil.toJsonStr(clone));
    }

    /**
     * 深克隆
     *
     * @throws CloneNotSupportedException
     */
    @Test
    public void testDeep() throws CloneNotSupportedException {
        DeepClone simpleClone = new DeepClone();
        simpleClone.setAge(1);
        User user = new User();
        user.setUsername("lhm");
        simpleClone.setUser(user);
        DeepClone clone = (DeepClone) simpleClone.clone();
        System.out.println("是否为同一个对象" + (clone == simpleClone));
        System.out.println("clone: " + JSONUtil.toJsonStr(clone));
        simpleClone.setAge(2);
        user.setUsername("lhm2");
        System.out.println("原始值修改: " + JSONUtil.toJsonStr(simpleClone));
        System.out.println("修改后clone: " + JSONUtil.toJsonStr(clone));
    }
}
