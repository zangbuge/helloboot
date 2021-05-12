package com.hugmount.helloboot.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/** SXSSFWorkbook优化版，处理大批量数据提升效率，节约内存
 * @Author： Li HuiMing
 * @Date: 2021/5/12
 */
public class POIUtil {

    public static SXSSFWorkbook exportExcel(LinkedHashMap<String, String> headMap, List<Map<String, Object>> dataList) {
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        // 打开压缩功能 防止占用过多磁盘
        workbook.setCompressTempFiles(true);

        // 设置表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        Font titleFont = workbook.createFont(); // 字体
        titleFont.setBold(true); // 是否加粗
        titleFont.setFontHeightInPoints((short) 14); // 几号字体
        // cellFont.setFontHeight((short) 380); // 按像素
        titleStyle.setFont(titleFont);

        // 内容样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font cellFont = workbook.createFont();
        cellFont.setBold(false);
        cellStyle.setFont(cellFont);

        // 创建一个sheet
        SXSSFSheet sheet = workbook.createSheet("Sheet1");
        // 锁定表头
        sheet.createFreezePane(0, 1, 0, 1);
        int headSize = headMap.size();

        // 创建表头
        Map<String, String> headOrder = new HashMap<>();
        SXSSFRow headRow = sheet.createRow(0);
        headRow.setHeight((short) 360); // 像素
        Iterator<Map.Entry<String, String>> iterator = headMap.entrySet().iterator();
        int ci = 0;
        while (iterator.hasNext()) {
            SXSSFCell cell = headRow.createCell(ci);
            Map.Entry<String, String> next = iterator.next();
            cell.setCellValue(next.getValue());
            cell.setCellStyle(titleStyle);
            headOrder.put(String.valueOf(ci), next.getKey());
            ci++;
        }

        // 创建数据行
        int dataSize = dataList.size();
        for (int i = 0; i < dataSize; i++) {
            Map<String, Object> map = dataList.get(i);
            SXSSFRow dataRow = sheet.createRow(i + 1);
            for (int j = 0; j < headSize; j++) {
                String dataKey = headOrder.get(String.valueOf(j));
                Object value = map.get(dataKey);
                String obj = Optional.ofNullable(value).orElse("").toString();
                SXSSFCell cell = dataRow.createCell(j);
                cell.setCellValue(obj);
                cell.setCellStyle(cellStyle);
            }
        }

        // 设置列宽
        for (int i = 0; i < headSize; i++) {
            // 宽为20个字符长度
            sheet.setColumnWidth(i, 256 * 20);
        }

        return workbook;
    }

    public static void downloadExcel(SXSSFWorkbook workbook, HttpServletResponse response, String fileName) {
        try {
            response.setContentType("application/msexcel;charset=utf-8");
            response.setHeader("Content-Disposition", "filename=" + new String(fileName.getBytes("utf-8")));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            // 释放workbook所占用的所有windows资源
            workbook.dispose();
        } catch (IOException e) {
            throw new RuntimeException("download excel exception");
        }
    }

}
