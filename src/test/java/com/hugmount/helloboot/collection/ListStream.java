package com.hugmount.helloboot.collection;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.test.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Li Huiming
 * @Date: 2021/2/10
 */

public class ListStream {
    public static void main(String[] args) {
        List<Student> list = new ArrayList<>();
        list.add(new Student(1, "产品", "女", 31, BigDecimal.valueOf(1)));
        list.add(new Student(2, "开发", "男", 27, BigDecimal.valueOf(1)));
        list.add(new Student(3, "测试", "女", 18, BigDecimal.valueOf(1)));
        list.add(new Student(4, "运维", "男", 36, BigDecimal.valueOf(1)));
        list.add(new Student(5, "运营", "男", 40, BigDecimal.valueOf(1)));
        list.add(new Student(6, "开发", "男", 28, BigDecimal.valueOf(1)));

        List<Student> sorted = list.stream().sorted(Comparator.comparing(Student::getAge)).collect(Collectors.toList());
        System.out.println("自定义升序排序 " + JSON.toJSONString(sorted));

        List<Student> reversed = list.stream().sorted(Comparator.comparing(Student::getAge).reversed()).collect(Collectors.toList());
        System.out.println("自定义倒序排序 " + JSON.toJSONString(reversed));

        List<Student> list1 = list.stream().filter(s -> s.sex.equals("男")).collect(Collectors.toList());
        System.out.println("过滤性别男");
        System.out.println(JSON.toJSONString(list1));

        Map<Integer, Student> map = list.stream().collect(Collectors.toMap(Student::getId, v -> v, (v1, v2) -> v1));
        Map<Integer, String> mapStr = list.stream().collect(Collectors.toMap(Student::getId, v -> v.getName(), (v1, v2) -> v1));
        System.out.println("list转map");
        System.out.println(JSON.toJSONString(map));
        System.out.println(JSON.toJSONString(mapStr));


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

        Map<String, Map<String, List<Student>>> collect5 = list.stream().collect(Collectors.groupingBy(Student::getSex, Collectors.groupingBy(Student::getName)));
        System.out.println("多字段分组");
        System.out.println(JSON.toJSONString(collect5));

        // stream.collect.groupingBy(分组字段，Collectors.reduce(bigdecimal.ZERO, 求和字段，Bigdecimal::add))
        Map<String, Integer> collect6 = list.stream().collect(Collectors.groupingBy(Student::getSex, Collectors.summingInt(Student::getAge)));
        System.out.println("分组求和");
        System.out.println(JSON.toJSONString(collect6));

        Map<String, Map<String, Object>> collect7 = list.stream().collect(Collectors.groupingBy(Student::getSex, Collectors.collectingAndThen(Collectors.toList(), m -> {
            BigDecimal reduce = m.stream().map(Student::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            int sum = m.stream().mapToInt(Student::getId).sum();
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("amountSum", reduce);
            resMap.put("idSum", sum);
            return resMap;
        })));
        System.out.println("分组后多列分别聚合操作");
        System.out.println(JSON.toJSONString(collect7));

        Map<String, Set<String>> collect3 = list.stream().collect(Collectors.groupingBy(Student::getSex, Collectors.mapping(Student::getName, Collectors.toSet())));
        System.out.println("分组后结果取对象中的一列转成set: " + JSON.toJSONString(collect3));

        collect3.forEach((key, val) -> {
            System.out.println("遍历map key" + key + ", val: " + val);
        });

        Map<String, Set<User>> collect4 = list.stream().collect(Collectors.groupingBy(Student::getSex, Collectors.mapping(item -> {
            String name = item.getName();
            User user = new User();
            user.setUsername(name);
            return user;
        }, Collectors.toSet())));
        System.out.println("分组并转换类型");
        System.out.println(JSON.toJSONString(collect4));
    }


    @Data
    @AllArgsConstructor
    static class Student {
        int id;
        String name;
        String sex;
        Integer age;
        BigDecimal amount;
    }

}
