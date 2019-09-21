package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Map;

/**
 * @Author Li Huiming
 * @Date 2019/9/19
 */

@Slf4j
public class HttpClientUtil {

    private static final String TYPE_GET = "get";
    private static final String TYPE_POST = "post";

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

    public static String doPost(String url, Map<String, Object> param, CloseableHttpClient httpClient) {
        return  sendRequestKeepAlive(httpClient, url, param, TYPE_POST, null);
    }

    public static String doGet(String url, Map<String, Object> param, CloseableHttpClient httpClient) {
        return  sendRequestKeepAlive(httpClient, url, param, TYPE_GET, null);
    }


    public static CloseableHttpClient getClient() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        return httpClient;
    }



    public static String sendRequestKeepAlive(CloseableHttpClient httpClient, String url, Map<String, Object> param
            ,String type ,Map<String, Object> header) {

        try {
            if (httpClient == null) {
                httpClient = getClient();
            }

            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key).toString());
                }
            }

            HttpResponse httpResponse;
            // 创建http GET 或 Post 请求
            URI uri = builder.build();
            if (TYPE_GET.equals(type)) {
                HttpGet httpGet = new HttpGet(uri);
                httpResponse = httpClient.execute(httpGet);
            }
            else {
                HttpPost httpPost = new HttpPost(uri);
                RequestConfig config = createConfig();
                httpPost.setConfig(config);
                // 设置请求头信息
                if (MapUtils.isNotEmpty(header)) {
                    for (Map.Entry<String, Object> map : header.entrySet()) {
                        httpPost.addHeader(map.getKey(), map.getValue().toString());
                    }
                }
                // 执行请求
                httpResponse = httpClient.execute(httpPost);
            }

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
