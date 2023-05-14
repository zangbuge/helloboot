package com.hugmount.helloboot.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author: lhm
 * @date: 2023/4/26
 */
public class PDFUtil {
    /**
     * pdf生成水印
     *
     * @param srcPdfPath       插入前的文件路径
     * @param tarPdfPath       插入后的文件路径
     * @param WaterMarkContent 水印文案
     * @param numberOfPage     每页需要插入的条数
     * @throws Exception
     */
    @SneakyThrows
    public static void addPDFWaterMark(String srcPdfPath, String tarPdfPath, String WaterMarkContent, int numberOfPage) {
        PdfReader reader = new PdfReader(srcPdfPath);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(tarPdfPath));
        PdfGState gs = new PdfGState();

        //设置字体
        BaseFont font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        // 设置透明度
        gs.setFillOpacity(0.4f);

        int total = reader.getNumberOfPages() + 1;
        PdfContentByte content;
        for (int i = 1; i < total; i++) {
            content = stamper.getOverContent(i);
            content.beginText();
            content.setGState(gs);
            //水印颜色
            content.setColorFill(BaseColor.DARK_GRAY);
            //水印字体样式和大小
            content.setFontAndSize(font, 35);
            //插入水印  循环每页插入的条数
            for (int j = 0; j < numberOfPage; j++) {
                // 文字
//                content.showTextAligned(Element.ALIGN_CENTER, WaterMarkContent, 300, 200 * (j + 1), 30);
                BufferedImage watermarkImage = ImageUtil.createWatermarkImage(WaterMarkContent);
                Image image = bufferedImage2Image(watermarkImage);
                content.addImage(image);
            }
            content.endText();
        }
        stamper.close();
    }

    public static Image bufferedImage2Image(BufferedImage bufferedImage) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            int n;
            byte[] buffer = new byte[4096];
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return Image.getInstance(output.toByteArray());
        } catch (Exception e) {
            new RuntimeException(e);
        }
        return null;
    }
}
