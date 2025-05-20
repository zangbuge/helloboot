package com.hugmount.helloboot.minio;

import io.minio.*;
import io.minio.http.Method;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author lhm
 * @date 2025/4/18
 */
public class TestMinio {
    @SneakyThrows
    public static void main(String[] args) {
        MinioClient minioClient = MinioClient.builder().credentials("lhm", "123456789").endpoint("http://192.168.38.128:9000").build();
        // 创建目录
        String bucketName = "test";
        BucketExistsArgs bucket = BucketExistsArgs.builder().bucket(bucketName).build();
        if (!minioClient.bucketExists(bucket)) {
            MakeBucketArgs make = MakeBucketArgs.builder().bucket(bucketName).build();
            minioClient.makeBucket(make);
        }
        // 多级目录可在文件名中用/分隔,而不是bucket
        String fileName = "1/wx.png";
        FileInputStream inputStream = new FileInputStream("D:/wx.png");
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .object(fileName) // 文件名
                .bucket(bucketName) // 桶名词 与minio创建的名词一致
                // fileInputStream.available() 表示一直有内容就会上传;  -1 表示将所有的相关文件的内容都上传
                .stream(inputStream, inputStream.available(), -1) //文件流
                .build();
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(putObjectArgs);

        // 获取文件下载地址
        GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET) // 下载地址的请求方式
                .bucket(bucketName)
                .object(fileName)
                .expiry(1, TimeUnit.HOURS) // 下载地址过期时间
                .build();
        String objectUrl = minioClient.getPresignedObjectUrl(urlArgs);
        System.out.println("下载地址: " + objectUrl);
    }

}
