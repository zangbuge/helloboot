package com.hugmount.helloboot;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.util.HttpUtil;
import org.junit.Test;

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
        header.put("token", "1234567890");
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "123");
        map.put("nickname", "李会明");
        String json = HttpUtil.sendPostJson(testJson, JSON.toJSONString(map), header);
        System.out.println("testJson返回: " + json);

        String post = HttpUtil.sendPostForm(testPost, map, header);
        System.out.println("testPost返回: " + post);

        String downloadUrl = "http://127.0.0.1:8001/downloadExcel";
        String s = HttpUtil.downloadFile(downloadUrl, map, header, "d:/excelTest.xlsx");
        System.out.println("下载文件" + s);

    }

    @Test
    public void testHuTool() {
        String url = "http://127.0.0.1:8001/testBody";
        Map<String, String> map = new HashMap<>();
        map.put("lhm", "123");
        String testlhm = HttpRequest.post(url).header("token", "hello").body(JSON.toJSONString(map)).execute().body();
        System.out.println(testlhm);
    }

}
