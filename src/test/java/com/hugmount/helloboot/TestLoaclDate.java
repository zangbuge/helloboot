package com.hugmount.helloboot;

import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * mysql使用LocalDate  要求数据库驱动版本最低为 5.1.37
 *
 * @Author: Li Huiming
 * @Date: 2020/11/28
 */
public class TestLoaclDate {
    @SneakyThrows
    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 时间格式化字符串
        String format = now.format(formatter);
        System.out.println(format);
        // 字符串转时间
        LocalDate parse = LocalDate.parse("2000-01-31", formatter);
        // 时间计算加减
        LocalDate localDate = parse.plusMonths(1);
        System.out.println(localDate.format(formatter));
        // 有坑并非累计月数 日期1传小 2传大
        int months = Period.between(parse, now).getMonths();
        int years = Period.between(parse, now).getYears();
        System.out.println("错误的之间月数 " + months);
        System.out.println("之间年数" + years);

        Date date = DateUtils.parseDate("2000-01-31", "yyyy-MM-mm");
        long l = DateUtil.betweenMonth(date, new Date(), false);
        System.out.println(l);

    }
}
