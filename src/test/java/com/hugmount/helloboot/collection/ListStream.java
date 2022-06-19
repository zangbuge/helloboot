package com.hugmount.helloboot.collection;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: Li Huiming
 * @Date: 2021/2/10
 */

public class ListStream {
    public static void main(String[] args) {
        List<Student> list = new ArrayList<>();
        list.add(new Student(1, "产品", "女", 31));
        list.add(new Student(2, "开发", "男", 27));
        list.add(new Student(3, "测试", "女", 18));
        list.add(new Student(4, "运维", "男", 36));
        list.add(new Student(5, "运营", "男", 40));

        List<Student> list1 = list.stream().filter(s -> s.sex.equals("男")).collect(Collectors.toList());
        System.out.println("过滤性别男");
        System.out.println(JSON.toJSONString(list1));

        Map<Integer, Student> map = list.stream().collect(Collectors.toMap(Student::getId, v -> v, (v1, v2) -> v1));
        System.out.println("list转map");
        System.out.println(JSON.toJSONString(map));

        List<Student> collect1 = map.entrySet().stream().map(item -> item.getValue())
                .collect(Collectors.toList());
        System.out.println("map转list");
        System.out.println(JSON.toJSONString(collect1));

        List<String> collect = list.stream().map(Student::getName).collect(Collectors.toList());
        System.out.println("只保留其中一列");
        System.out.println(JSON.toJSONString(collect));

        System.out.println("forEach");
        list.stream().forEach(item -> {
            System.out.println(JSON.toJSONString(item));
        });

        Map<String, List<Student>> collect2 = list.stream().collect(Collectors.groupingBy(Student::getSex));
        System.out.println("分组");
        System.out.println(JSON.toJSONString(collect2));

        Map<String, Set<String>> collect3 = list.stream().collect(Collectors.groupingBy(Student::getSex, Collectors.mapping(Student::getName, Collectors.toSet())));
        System.out.println(JSON.toJSONString(collect3));

    }


    @Data
    @AllArgsConstructor
    static class Student {
        int id;
        String name;
        String sex;
        Integer age;
    }

}
