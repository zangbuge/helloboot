package com.hugmount.helloboot;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Li Huiming
 * @Date: 2020/5/23
 */
public class TestHttp {
    public static void main(String[] args) {
        String testJson = "http://127.0.0.1:8001/testJson";
        String testPost = "http://127.0.0.1:8001/testPost";
        Map<String, Object> header = new HashMap<>();
        header.put("token","1234567890");
        Map<String, Object> map = new HashMap<>();
        map.put("userId","123");
        map.put("nickname","李会明");
        String json = HttpUtil.sendPost(testJson, JSON.toJSONString(map), header);
        System.out.println("testJson返回: " + json);

        String post = HttpUtil.sendPost(testPost, map, header);
        System.out.println("testPost返回: " + post);

    }
}
