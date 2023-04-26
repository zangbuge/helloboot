package com.hugmount.helloboot.util;

import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static java.awt.Color.lightGray;

/**
 * Excel添加水印
 *
 * @author: lhm
 * @date: 2023/4/26
 */
public class ImageUtil {

    /**
     * 给 Excel 添加水印
     *
     * @param workbook      SXSSFWorkbook
     * @param waterMarkText 水印文字内容
     */
    @SneakyThrows
    public static void insertWaterMarkTextToXlsx(SXSSFWorkbook workbook, String waterMarkText) {
        BufferedImage image = createWatermarkImage(waterMarkText);
        ByteArrayOutputStream imageOs = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageOs);
        int pictureIdx = workbook.addPicture(imageOs.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG);
        XSSFPictureData pictureData = (XSSFPictureData) workbook.getAllPictures().get(pictureIdx);
        // 获取每个Sheet表
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            SXSSFSheet sheet = workbook.getSheetAt(i);
            // 这里由于 SXSSFSheet 没有 getCTWorksheet() 方法，通过反射取出 _sh 属性
            XSSFSheet shReflect = (XSSFSheet) ReflectUtil.getFieldValue(sheet, "_sh");
            PackagePartName ppn = pictureData.getPackagePart().getPartName();
            String relType = XSSFRelation.IMAGES.getRelation();
            PackageRelationship pr = shReflect.getPackagePart().addRelationship(ppn, TargetMode.INTERNAL, relType, null);
            shReflect.getCTWorksheet().addNewPicture().setId(pr.getId());
        }
    }


    /**
     * 给 Excel 添加水印
     *
     * @param workbook      XSSFWorkbook
     * @param waterMarkText 水印文字内容
     */
    @SneakyThrows
    public static void insertWaterMarkTextToXlsx(XSSFWorkbook workbook, String waterMarkText) {
        BufferedImage image = createWatermarkImage(waterMarkText);
        ByteArrayOutputStream imageOs = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageOs);
        int pictureIdx = workbook.addPicture(imageOs.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG);
        XSSFPictureData pictureData = workbook.getAllPictures().get(pictureIdx);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            PackagePartName ppn = pictureData.getPackagePart().getPartName();
            String relType = XSSFRelation.IMAGES.getRelation();
            PackageRelationship pr = sheet.getPackagePart().addRelationship(ppn, TargetMode.INTERNAL, relType, null);
            sheet.getCTWorksheet().addNewPicture().setId(pr.getId());
        }
    }

    /**
     * 创建水印图片
     *
     * @param waterMark 水印文字
     */
    public static BufferedImage createWatermarkImage(String waterMark) {
        String[] textArray = waterMark.split("\n");
        Font font = new Font("microsoft-yahei", Font.PLAIN, 32);
        int width = 500;
        int height = 400;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 背景透明 开始
        Graphics2D graphics = image.createGraphics();
        image = graphics.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        graphics.dispose();
        // 背景透明 结束
        graphics = image.createGraphics();
        graphics.setColor(new Color(lightGray.getRGB()));// 设定画笔颜色
        graphics.setFont(font);// 设置画笔字体
        // graphics.shear(0.1, -0.26);// 设定倾斜度
        // 设置字体平滑
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //文字从中心开始输入，算出文字宽度，左移动一半的宽度，即居中
        FontMetrics fontMetrics = graphics.getFontMetrics(font);

        // 水印位置
        int x = width / 2;
        int y = height / 2;
        // 设置水印旋转
        graphics.rotate(Math.toRadians(-40), x, y);
        for (String s : textArray) {
            // 文字宽度
            int textWidth = fontMetrics.stringWidth(s);
            graphics.drawString(s, x - (textWidth / 2), y);// 画出字符串
            y = y + font.getSize();
        }

        graphics.dispose();// 释放画笔
        return image;
    }

    /**
     * 设置打印的参数
     *
     * @param wb XSSFWorkbook
     */
    public static void setPrintParams(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        // 打印方向，true：横向，false：纵向(默认
        printSetup.setLandscape(true);
        //设置A4纸
        printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
        // 将整个工作表打印在一页（缩放）,如果行数很多的话，可能会出问题
        // sheet.setAutobreaks(true);
        //将所有的列调整为一页，行数多的话，自动分页
        printSetup.setScale((short) 70);//缩放的百分比，自行调整
        sheet.setAutobreaks(false);
    }

}
