package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * @Author Li Huiming
 * @Date 2019/9/19
 */

@Slf4j
public class HttpClientUtil {

    private static CloseableHttpClient httpClient;

    static {
        httpClient = getClient();
    }

    /**
     * 发送post请求 json数据格式
     *
     * @param url
     * @param json
     * @param header
     * @return
     */
    public static String doPostJson(String url, String json, Map<String, Object> header) {
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            RequestConfig config = createConfig();
            httpPost.setConfig(config);
            if (MapUtils.isNotEmpty(header)) {
                for (Map.Entry<String, Object> map : header.entrySet()) {
                    httpPost.addHeader(map.getKey(), map.getValue().toString());
                }
            }
            // 执行http请求
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // EntityUtils.toString()会关闭流且释放连接
            String result = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("httpClient请求异常", e);
        }
    }

    /**
     * 发送post请求 表单数据格式,可传文件
     *
     * @param url
     * @param file
     * @param name
     * @param fromData
     * @param header
     * @return
     */
    public static String doPostForm(String url, File file, String name, Map<String, Object> fromData, Map<String, Object> header) {
        try {
            // 相当于<input type="file" name="file"/>
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Consts.UTF_8);
            // 以浏览器兼容模式运行，防止文件名乱码。必须
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody(name, new FileInputStream(file), ContentType.DEFAULT_BINARY, file.getName());
            for (Map.Entry<String, Object> entry : fromData.entrySet()) {
                String key = entry.getKey();
                String obj = entry.getValue().toString();
                // 相当于<input type="text" name="userName" value=userName>
                StringBody value = new StringBody(obj, ContentType.create("text/plain", Consts.UTF_8));
                builder.addPart(key, value);
            }
            HttpEntity entity = builder.build();
            HttpPost httpPost = new HttpPost(url);
            if (MapUtils.isNotEmpty(header)) {
                for (Map.Entry<String, Object> map : header.entrySet()) {
                    httpPost.addHeader(map.getKey(), map.getValue().toString());
                }
            }
            httpPost.setEntity(entity);
            // 发起请求 并返回请求的响应
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // 获取响应对象
            String res = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            return res;
        } catch (Exception e) {
            throw new RuntimeException("httpClient上传文件异常", e);
        }
    }

    public static CloseableHttpClient getClient() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        //检测有效链接的间隔
        manager.setValidateAfterInactivity(9000);
        //设定链接池最大数量
        manager.setMaxTotal(500);
        //设定默认单个路由的最大链接数（因为本处只使用一个路由地址因此设定为链接池大小）
        manager.setDefaultMaxPerRoute(1000);
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableAutomaticRetries()
                .setConnectionManager(manager)
                .build();
        return httpClient;
    }

    private static RequestConfig createConfig() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1000 * 6)
                .setConnectionRequestTimeout(1000 * 2)
                .setSocketTimeout(1000 * 30)
                .build();
        return config;
    }

}
