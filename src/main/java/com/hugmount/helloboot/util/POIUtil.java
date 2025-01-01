package com.hugmount.helloboot.util;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * SXSSFWorkbook优化版，处理大批量数据提升效率，节约内存
 *
 * @Author： Li HuiMing
 * @Date: 2021/5/12
 */
public class POIUtil {

    private POIUtil() {
    }

    private static String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    private static String DEFAULT_SHEET_NAME = "Sheet1";

    /**
     * 导出excel
     *
     * @param headMap
     * @param dataList
     * @return
     */
    public static SXSSFWorkbook exportExcel(Map<String, Object> headMap, List<Map<String, Object>> dataList) {
        return exportExcel(null, headMap, dataList, DEFAULT_SHEET_NAME, 0);
    }

    public static SXSSFWorkbook exportExcel(SXSSFWorkbook workbook, Map<String, Object> headMap, List<Map<String, Object>> dataList
            , String sheetName, int startRowNo) {
        // 声明一个工作薄
        if (workbook == null) {
            workbook = new SXSSFWorkbook();
        }
        // 打开压缩功能 防止占用过多磁盘
        workbook.setCompressTempFiles(true);

        // 表头样式
        Font titleFont = workbook.createFont(); // 字体
        titleFont.setFontHeightInPoints((short) 12); // 按几号字体
        // cellFont.setFontHeight((short) 380); // 按像素
        titleFont.setBold(true); // 是否加粗
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        titleStyle.setFont(titleFont);

        // 内容样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font cellFont = workbook.createFont();
        cellFont.setBold(false);
        cellStyle.setFont(cellFont);
        // 设置单元格格式为文本
        DataFormat dataFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("@"));

        // 创建一个sheet
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        // 锁定表头
        // 参数1: 要冻结的列数
        // 参数2: 要冻结的行数
        sheet.createFreezePane(0, 1);
        int headSize = headMap.size();
        // 创建表头并设置顺序
        Map<String, String> headOrder = new HashMap<>();
        SXSSFRow headRow = sheet.createRow(startRowNo);
        Iterator<Map.Entry<String, Object>> iterator = headMap.entrySet().iterator();
        int ci = 0;
        while (iterator.hasNext()) {
            SXSSFCell cell = headRow.createCell(ci);
            Map.Entry<String, Object> next = iterator.next();
            cell.setCellValue(String.valueOf(next.getValue()));
            cell.setCellStyle(titleStyle);
            headOrder.put(String.valueOf(ci), next.getKey());
            ci++;
        }

        // 创建数据行
        int dataSize = dataList.size();
        for (int i = 0; i < dataSize; i++) {
            Map<String, Object> map = dataList.get(i);
            SXSSFRow dataRow = sheet.createRow(i + 1 + startRowNo);
            for (int j = 0; j < headSize; j++) {
                String dataKey = headOrder.get(String.valueOf(j));
                String obj = (String) map.get(dataKey);
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

    @SneakyThrows(Exception.class)
    public static void downloadExcel(SXSSFWorkbook workbook, HttpServletResponse response, String fileName) {
        // 处理中文乱码
        fileName = URLEncoder.encode(fileName, "utf-8");
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-Disposition", "filename=" + fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        // 释放workbook所占资源
        workbook.dispose();
    }

    /**
     * 解析excel
     *
     * @param inputStream
     * @return
     */
    public static List<Map<String, Object>> importExcel(InputStream inputStream) {
        List<Map<String, Object>> list = importExcel(inputStream, 0, true);
        list.remove(0);
        return list;
    }

    @SneakyThrows(Exception.class)
    public static List<Map<String, Object>> importExcel(InputStream inputStream, int startRow, boolean useHeadName) {
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(wb);
        XSSFWorkbook xssfWorkbook = sxssfWorkbook.getXSSFWorkbook();
        XSSFSheet sheetAt = xssfWorkbook.getSheetAt(0);
        if (sheetAt == null) {
            return null;
        }
        Map<String, Object> headerMap = new HashMap<>();
        List<Map<String, Object>> rowList = new ArrayList<>();
        // 获取的lastRowNum比实际行数少一行,表头
        int lastRowNum = sheetAt.getLastRowNum();
        for (int i = startRow; i <= lastRowNum; i++) {
            XSSFRow row = sheetAt.getRow(i);
            if (row == null) {
                continue;
            }
            // 创建保存一行数据map
            Map<String, Object> rowData = dealRowData(row, headerMap);
            rowList.add(rowData);
            // 使用表头为数据的key
            if (useHeadName && i == startRow) {
                headerMap = rowData;
            }
        }
        return rowList;
    }

    static Map<String, Object> dealRowData(XSSFRow row, Map<String, Object> headerMap) {
        short lastCellNum = row.getLastCellNum();
        Map<String, Object> rowData = new LinkedHashMap<>();
        boolean useHead = headerMap != null && headerMap.size() > 0;
        for (short j = 0; j < lastCellNum; j++) {
            XSSFCell cell = row.getCell(j);
            String cellValueStr = getCellValueStr(cell);
            // 使用表头为key
            String key = null;
            if (useHead) {
                key = (String) headerMap.get(String.valueOf(j));
            }
            // 使用列序号为key
            if (key == null || key.trim().length() == 0) {
                key = String.valueOf(j);
            }
            rowData.put(key, cellValueStr);
        }
        return rowData;
    }

    static String getCellValueStr(XSSFCell cell) {
        if (null == cell) {
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN: // 布尔值
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: // 读取公式
                return cell.getCellFormula();
            case NUMERIC: // 日期或数字
                // 读取日期
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date dateCellValue = cell.getDateCellValue();
                    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STR);
                    return dateFormat.format(dateCellValue);
                }
                // 读取数字
                double number = cell.getNumericCellValue();
                BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(number));
                BigDecimal bigDecimal2 = new BigDecimal((long) number);
                // 整数
                if (bigDecimal1.compareTo(bigDecimal2) == 0) {
                    return bigDecimal2.toString();
                }
                // 包含小数的double, 设置diuble类型不转为"科学计数法"
                DecimalFormat df = new DecimalFormat("0.#############");
                return df.format(cell.getNumericCellValue());

            // 默认转换成String
            default:
                return cell.getStringCellValue();
        }

    }

    public static Map<String, Object> getOldHeadMap(Map<String, Object> map) {
        Map<String, Object> head = new LinkedHashMap<>();
        map.forEach((k, v) -> head.put(String.valueOf(v), v));
        return head;
    }

}
