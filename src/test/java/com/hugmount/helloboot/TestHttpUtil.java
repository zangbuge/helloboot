package com.hugmount.helloboot;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.util.HttpClientUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Li Huiming
 * @Date 2019/9/17
 */


public class TestHttpUtil {

    public static void main(String[] args) {

        String testJson = "http://127.0.0.1:8001/testJson";
        String testPost = "http://127.0.0.1:8001/testPost";
        Map<String, Object> header = new HashMap<>();
        header.put("token","1234567890");
        Map<String, Object> map = new HashMap<>();
        map.put("userId","123");
        map.put("nickname","李会明");
        CloseableHttpClient client = HttpClientUtil.getClient();
        String json = HttpClientUtil.doPostJson(testJson, JSON.toJSONString(map), null);
        System.out.println("testJson返回: " + json);

        String post = HttpClientUtil.doPost(testPost, map,null);
        System.out.println("testPost返回" + post);

        String s = HttpClientUtil.sendRequestKeepAlive(null, testPost, map, "post", header);
        System.out.println(s);

        String ss = HttpClientUtil.doPostJson(testJson, JSON.toJSONString(map), header);
        System.out.println(ss);

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


