package com.hugmount.helloboot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author: lhm
 * @date: 2023/3/16
 */
public class ReadFileTest {

    static String path = "d:/yanshou";
    static String targetName = "接口设计.docx";
    static String targetPath = "d:/yanshou_all/";

    public static void main(String[] args) throws Exception {
        File file = new File(path);
        readFile(file);
    }

    public static void readFile(File file) throws Exception {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                readFile(files[i]);
            }
        }
        String name = file.getName();
        String path = file.getPath();
        String fullName = path + "/" + name;
        System.out.println("当前文件命名: " + fullName);
        if (targetName.equals(name)) {
            String replace = fullName.replace("\\", "_").replace("/", "_");
            int index = replace.lastIndexOf("_", 3);
            String substring = replace.substring(index + 1).trim();
            String targetFile = targetPath + substring;
            try (FileChannel channel1 = new FileInputStream(file).getChannel();
                 FileChannel channel2 = new FileOutputStream(targetFile).getChannel()) {
                channel2.transferFrom(channel1, 0, channel1.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

}
