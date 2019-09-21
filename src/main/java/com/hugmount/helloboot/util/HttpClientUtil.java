package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Li Huiming
 * @Date 2019/9/19
 */

@Slf4j
public class HttpClientUtil {

    public static String doPostJson(String url, String json, CloseableHttpClient httpClient) {
        if (null == httpClient) {
            // 创建Httpclient对象
            httpClient = HttpClients.createDefault();
        }

        CloseableHttpResponse response = null;
        String resultStr = null;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultStr = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultStr;
    }



    public static CloseableHttpClient getClient() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        return httpClient;
    }


    /**
     * 不可用
     * @return
     */
    public static String sendPostKeepAlive(CloseableHttpClient httpClient, String url, Map<String, Object> param
            ,Map<String, Object> header) {

        try {
            if (httpClient == null) {
                httpClient = getClient();
            }
            HttpPost httpPost = new HttpPost(url);
            RequestConfig config = createConfig();
            httpPost.setConfig(config);
            // 设置请求头信息
            if (MapUtils.isNotEmpty(header)) {
                for (Map.Entry<String, Object> map : header.entrySet()) {
                    httpPost.addHeader(map.getKey(), map.getValue().toString());
                }
            }
            // 设置请求参数
            if (MapUtils.isNotEmpty(param)) {
                List<NameValuePair> formParams = new ArrayList<>();
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    //给参数赋值
                    BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                    formParams.add(basicNameValuePair);
                }
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formParams, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
            }
            // 执行请求
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            String res = EntityUtils.toString(entity);
            return res;
        } catch (Exception e) {
            log.error("请求失败,url: {}", url, e);
        }
        return null;
    }

    private static RequestConfig createConfig() {
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(1000 * 6)
                .setConnectionRequestTimeout(1000 * 2)
                .setRedirectsEnabled(false) // 配置禁用30x 跳转
                .setSocketTimeout(1000 * 30)
                .build();
        return builder.build();
    }

}
