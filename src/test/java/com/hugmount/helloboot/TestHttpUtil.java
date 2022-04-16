package com.hugmount.helloboot;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.util.HttpClientUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Li Huiming
 * @Date 2019/9/17
 */


public class TestHttpUtil {

    public static void main(String[] args) {

        String testJson = "http://127.0.0.1:8001/testJson";
        Map<String, Object> header = new HashMap<>();
        header.put("token", "1234567890");
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "123");
        map.put("nickname", "李会明");
        String json = HttpClientUtil.doPostJson(testJson, JSON.toJSONString(map), header);
        System.out.println("testJson返回: " + json);

        String testFile = "http://127.0.0.1:8001/testFile";
        File file = new File("d:/wx.png");
        String file1 = HttpClientUtil.doPostForm(testFile, file, "file", map, header);
        System.out.println("testFile返回: " + file1);

    }

}


