package com.hugmount.helloboot;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.util.CommonsHTTPUtil;
import com.hugmount.helloboot.util.HttpClientUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Li Huiming
 * @Date 2019/9/17
 */


public class TestHttpUtil {

    public static void main(String[] args) {

        String urltest = "http://localhost:8080/test";
        String urlhello = "http://localhost:8080/hello";
        Map<String, Object> header = new HashMap<>();
        header.put("accept", "text/plain;charset=utf-8");
        header.put("connection", "Keep-Alive");
        header.put("Content-Type", "application/json");
        header.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        Map<String, Object> map = new HashMap<>();
        map.put("productName","hello");
        map.put("password","password");
        map.put("username","李会明");
        String urlLogin = "http://localhost:8086/helloboot/login";
        CloseableHttpClient client = HttpClientUtil.getClient();
//        String s1 = HttpClientUtil.doPost(urltest, map, null);
        File file = new File("d:/正面.jpg");
//        String s1 = HttpClientUtil.uploadFile(urltest, file ,"pic" ,map, null);
        String s1 = HttpClientUtil.doPostJson(urlhello, JSON.toJSONString(map),null);
//        String s1 = CommonsHTTPUtil.doPost(urltest, map, null);
        System.out.println("登录返回" + s1);



        String url = "http://localhost:8086/helloboot/product/getProductList";
        header.put("accept", "*/*");
        header.put("connection", "Keep-Alive");
        header.put("Content-Type", "application/json");
        header.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 发送POST请求必须设置如下两行
//        String s = HttpClientUtil.sendPostKeepAlive(client, url ,map ,null);
//        System.out.println(s);
//        System.out.println("success");
    }

}


