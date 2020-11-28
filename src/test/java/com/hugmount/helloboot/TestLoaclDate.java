package com.hugmount.helloboot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** mysql使用LocalDate  要求数据库驱动版本最低为 5.1.37
 * @Author: Li Huiming
 * @Date: 2020/11/28
 */
public class TestLoaclDate {
    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 时间格式化字符串
        String format = now.format(formatter);
        System.out.println(format);
        // 字符串转时间
        LocalDate parse = LocalDate.parse("2020-01-31", formatter);
        // 时间计算加减
        LocalDate localDate = parse.plusMonths(1);
        System.out.println(localDate.format(formatter));
    }
}
