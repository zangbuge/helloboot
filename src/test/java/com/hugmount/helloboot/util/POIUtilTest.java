package com.hugmount.helloboot.util;

import lombok.SneakyThrows;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author lhm
 * @date 2023/11/23
 */
public class POIUtilTest {

    @SneakyThrows
    @Test
    public void testExcel() {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\lhm\\Desktop/测试数据.xlsx");
        List<Map<String, Object>> maps = POIUtil.importExcel(fileInputStream, 0, true);
        Map<String, Object> oldHeadMap = POIUtil.getOldHeadMap(maps.get(0));
        maps.remove(0);
        for (Map<String, Object> map : maps) {
            map.put("测试1", UUID.randomUUID().toString());
            map.put("测试2", UUID.randomUUID().toString());
            map.put("测试3", UUID.randomUUID().toString());
        }
        oldHeadMap.put("测试1", "测试1");
        oldHeadMap.put("测试3", "测试3");
        SXSSFWorkbook sheets = POIUtil.exportExcel(oldHeadMap, maps);
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\lhm\\Desktop/测试数据添加字段.xlsx");
        sheets.write(fileOutputStream);
    }
}