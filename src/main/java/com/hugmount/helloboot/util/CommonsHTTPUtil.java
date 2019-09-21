package com.hugmount.helloboot.util;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @Author Li Huiming
 * @Date 2019/9/21
 */

public class CommonsHTTPUtil {

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url  请求的URL地址
     * @param params  请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static String doPost(String url, Map<String, Object> params, HttpClient client) {

        StringBuffer response = new StringBuffer();
        PostMethod method = new PostMethod(url);

        if (null == client) {
            client = new HttpClient();
        }

        // 设置Http Post数据
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                method.setParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        try {
            HttpConnectionManager connectionManager = new SimpleHttpConnectionManager(false);
            client.setHttpConnectionManager(connectionManager);
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                String charset = "UTF-8";
                // 读取为 InputStream，在网页内容数据量大时候推荐使用
                InputStreamReader inputStreamReader = new InputStreamReader(method.getResponseBodyAsStream(), charset);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
//            method.releaseConnection();
        }
        return response.toString();
    }

}
