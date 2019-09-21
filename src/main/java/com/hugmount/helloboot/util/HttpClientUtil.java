package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.charset.Charset;
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



    public static String uploadFile(String url, File file, String name, Map<String, Object> param
            , CloseableHttpClient httpClient) {

        CloseableHttpResponse response;
        try {
            if (null == httpClient) {
                httpClient = getClient();
            }

            HttpPost httpPost = new HttpPost(url);
            // 相当于<input type="file" name="file"/>
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            //以浏览器兼容模式运行，防止文件名乱码。必须
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody(name, new FileInputStream(file), ContentType.DEFAULT_BINARY, file.getName());
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                String key = entry.getKey();
                String obj = entry.getValue().toString();

                // 相当于<input type="text" name="userName" value=userName>
                StringBody value = new StringBody(obj, ContentType.create("text/plain", Consts.UTF_8));
                builder.addPart(key, value);
            }
            HttpEntity reqEntity = builder.build();
            httpPost.setEntity(reqEntity);
            // 发起请求 并返回请求的响应
            response = httpClient.execute(httpPost);
            // 获取响应对象
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                return EntityUtils.toString(resEntity, Consts.UTF_8);
            }
        }catch (Exception e) {
            log.error("上传文件失败", e);
        }
        return null;
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
            String res = EntityUtils.toString(entity, HTTP.UTF_8);
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
