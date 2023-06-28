package com.hugmount.helloboot;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;

/**
 * @author: lhm
 * @date: 2023/6/21
 */
@Slf4j
public class TestImg {
    public static void main(String[] args) {
        TestImg testImg = new TestImg();
        InputStream inputStream = testImg.getClass().getResourceAsStream("/store/123.png");
        String s = testImg.img2Base64(inputStream);
        System.out.println(s);
        testImg.base64ToFile(s, "d:/test123.jpg");
        System.out.println("base64转图片成功");

        File file = new File("d:/hello");
        boolean exists = file.exists();
        System.out.println("文件是否存在" + exists);
        if (!exists) {
            /// file.mkdir(); 只能创建一级文件夹
            file.mkdirs();
        }

    }

    public String img2Base64(InputStream inputStream) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage read = ImageIO.read(inputStream);
            ImageIO.write(read, "jpg", outputStream);
            byte[] bytes = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return base64;
        } catch (Exception e) {
            log.error("img2Base64 error", e);
        }
        return null;
    }

    public void base64ToFile(String base64, String filePath) {
        // 获取图片类型
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        byte[] bytes = Base64.getDecoder().decode(base64);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            File file = new File(filePath);
            ImageIO.write(bufferedImage, suffix, file);
        } catch (Exception e) {
            log.error("base642File error", e);
            throw new RuntimeException(e);
        }
    }
}
