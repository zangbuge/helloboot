package com.hugmount.helloboot.util;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * @author lhm
 * @date 2025/2/28
 */
public class KryoSerializationUtil {

    // 序列化对象到 byte[]
    public static byte[] serialize(Object obj) {
        Kryo kryo = new Kryo();
        // 注册需要序列化的类（如果需要的话,泛型如List）
        // kryo.register(YourClass.class);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output, obj);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    // 从byte[]反序列化对象
    public static <T> T deserialize(byte[] byteArray, Class<T> clazz) {
        Kryo kryo = new Kryo();
        // kryo.register(YourClass.class);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        Input input = new Input(byteArrayInputStream);
        T obj = kryo.readObject(input, clazz);
        input.close();
        return obj;
    }

    // 示例类，用于测试序列化和反序列化
    public static class Example implements Serializable {
        private static final long serialVersionUID = 1L;
        private int id;
        private String name;

        public Example() {

        }

        // 构造函数、getter和setter方法
        public Example(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Example{id=" + id + ", name='" + name + "'}";
        }
    }

    public static void main(String[] args) {
        Example example = new Example(1, "Test");

        // 序列化对象
        byte[] serializedData = serialize(example);
        System.out.println("Serialized Data: " + java.util.Arrays.toString(serializedData));

        // 反序列化对象
        Example deserializedExample = deserialize(serializedData, Example.class);
        System.out.println("Deserialized Object: " + deserializedExample);
    }
}
