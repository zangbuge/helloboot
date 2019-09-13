package com.hugmount.helloboot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @Author Li Huiming
 * @Date 2019/9/10
 */

@Slf4j
@Data
public class TestSerializable implements Serializable{
    private static final long serialVersionUID = 5887391604554532906L;

    private int id;

    private String name;

    private String age;

    public TestSerializable(int id, String name, String age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        try {
            //序列化
            FileOutputStream fileOutputStream = new FileOutputStream("test.obj");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject("hello world");

            TestSerializable lhm = new TestSerializable(123,"lhm","26");
            objectOutputStream.writeObject(lhm);

            //反序列化, 读取时按写入的顺序读取对象
            FileInputStream fileInputStream = new FileInputStream("test.obj");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            String str = (String) objectInputStream.readObject();
            log.info(str);

            Object readObject = objectInputStream.readObject();
            TestSerializable test = (TestSerializable)readObject;
            log.info(test.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
