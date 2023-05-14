package com.hugmount.helloboot.util;

import org.junit.Test;

/**
 * @author: lhm
 * @date: 2023/4/26
 */
public class PDFUtilTest {

    @Test
    public void addPDFWaterMark() {
        String srcImgPath = "D:/file1.pdf"; // 源地址
        String tarImgPath = "D:/file2.pdf"; // 待存储的地址
        String msg = "版权测试水印"; // 水印内容
        PDFUtil.addPDFWaterMark(srcImgPath, tarImgPath, msg, 5);
        System.out.println("处理成功");
    }

}