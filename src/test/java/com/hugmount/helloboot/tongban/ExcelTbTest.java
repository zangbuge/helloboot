package com.hugmount.helloboot.tongban;

import com.hugmount.helloboot.util.POIUtil;
import lombok.SneakyThrows;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * @author lhm
 * @date 2024/12/11
 */
public class ExcelTbTest {

    @SneakyThrows
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("项目组长");
        list.add("销售协调人");
        list.add("交付协调人");
        list.add("咨询协调人");
        list.add("产品协调人");
        list.add("智助协调人");
        list.add("智办协调人");
        list.add("研发协调人");
        list.add("测试协调人");
        list.add("运维协调人");
        list.add("UI协调人");

        FileInputStream fileInputStream = new FileInputStream("D:\\项目清单/商机交付小组导出1211.xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\项目清单/商机交付小组清单.xlsx");
        List<Map<String, Object>> maps = POIUtil.importExcel(fileInputStream, 0, true);
        Map<String, Object> oldHeadMap = new LinkedHashMap<>();
        oldHeadMap.put("项目名称", "项目名称");
        oldHeadMap.put("项目类型", "项目类型");
        oldHeadMap.put("项目角色", "项目角色");
        oldHeadMap.put("是否需要该角色", "是否需要该角色");
        oldHeadMap.put("姓名", "姓名");
        List<Map<String, Object>> resList = new ArrayList<>();
        maps.remove(0);
        maps.forEach(item -> {
            String projectName = (String) item.get("项目名称");
            String teamName = (String) item.get("项目组长");
            String marketingName = (String) item.get("销售协调人");
            String type = (String) item.get("项目类型");
            String projectType = Optional.ofNullable(type).orElse("主小组");
            list.forEach(p -> {
                Map<String, Object> row = new HashMap<>();
                row.put("项目名称", projectName);
                row.put("项目角色", p);
                row.put("是否需要该角色", "");
                row.put("项目类型", projectType);
                if ("项目组长".equals(p)) {
                    row.put("姓名", teamName);
                    row.put("是否需要该角色", "是");
                }
                if ("销售协调人".equals(p) && marketingName != null) {
                    row.put("姓名", marketingName);
                    row.put("是否需要该角色", "是");
                }
                resList.add(row);

            });

        });

        SXSSFWorkbook sheets = POIUtil.exportExcel(oldHeadMap, resList);
        sheets.write(fileOutputStream);
    }

}
