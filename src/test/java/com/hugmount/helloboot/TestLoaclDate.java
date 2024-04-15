package com.hugmount.helloboot;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

        Date date = DateUtils.parseDate("2000-01-31", "yyyy-MM-dd");
        long l = DateUtil.betweenMonth(date, new Date(), false);
        System.out.println(l);

        // Date to LocalDateTime
        Date curDate = new Date();
        // 将 Instant 转换为 LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(curDate.toInstant(), ZoneId.systemDefault());
        System.out.println("Date：" + localDateTime);

        // LocalDateTime to Date
        LocalDateTime localDateTime2 = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime2.atZone(ZoneId.systemDefault());
        Date date2 = Date.from(zonedDateTime.toInstant());
        System.out.println("LocalDateTime：" + date2);

        LocalDate monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        System.out.println("当前周星期一" + monday);

        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        System.out.println("当前月1号: " + firstDayOfMonth);

        DateTime dateTime = DateUtil.beginOfQuarter(new Date());
        System.out.println("hutool获取某季度的开始时间: " + dateTime);
        // DateUtil.betweenDay()


    }
}
