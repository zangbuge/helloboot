package com.hugmount.helloboot.tongban;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hugmount.helloboot.util.POIUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 工作日历
 *
 * @author lhm
 * @date 2024/12/11
 */
public class DateTest {

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        List<DateTime> dateTimes = DateUtil.rangeToList(DateUtils.parseDate("2024-10-08", "yyyy-MM-dd"), DateUtils.parseDate("2026-01-01", "yyyy-MM-dd"), DateField.DAY_OF_YEAR);
        List<Map<String, Object>> collect = dateTimes.stream().map(it -> {
            Map<String, Object> map = new LinkedHashMap<>();
            String format = DateUtil.format(it, "yyyy/MM/dd");
            map.put("日期", format);
            try {
                int dayOfWeek = DateUtil.dayOfWeek(DateUtils.parseDate(format, "yyyy/MM/dd")) - 1;
                map.put("周几", dayOfWeek);
                map.put("类型", "工作日");
                if (dayOfWeek == 0 || dayOfWeek == 6) {
                    map.put("类型", "休息日");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return map;
        }).collect(Collectors.toList());

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("日期", "日期");
        header.put("周几", "周几");
        header.put("类型", "类型");
        SXSSFWorkbook sheets = POIUtil.exportExcel(header, collect);
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\temp/工作日历.xlsx");
        sheets.write(fileOutputStream);
//        collect.forEach(item -> System.out.println(item));
//        System.out.println(JsonUtil.toJson(collect));
    }

}
