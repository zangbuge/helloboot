package com.hugmount.helloboot;


import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Author: Li Huiming
 * @Date: 2021/2/22
 */
public class TestOkhttp {

    /**
     * POST提交Json数据
     */
    @Test
    public void testPostJson() {
        MediaType parse = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        HashMap<Object, Object> map = new HashMap<>();
        map.put("id", "123");
        String url = "http://localhost:8001/testJson";
        RequestBody body = RequestBody.create(parse, JSON.toJSONString(map));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("token", "lhm")
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            String result = execute.body().string();
            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
