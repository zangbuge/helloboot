package com.hugmount.helloboot.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Li Huiming
 * @Date 2019/9/19
 */

@Slf4j
public class HttpClientUtil {

    private HttpClientUtil() {
    }

    private static CloseableHttpClient httpClient;

    static {
        httpClient = getClient();
    }


    public static String doGet(String url, Map<String, Object> header) {
        return doGet(url, header, null);
    }

    public static String doPostJson(String url, String json, Map<String, Object> header) {
        return doPostJson(url, json, header, null);
    }

    public static String doPostForm(String url, Map<String, Object> param, Map<String, Object> header) {
        return doPostForm(url, param, header, null);
    }

    /**
     * 发送post请求 json数据格式
     *
     * @param url
     * @param json
     * @param header
     * @return
     */
    public static String doPostJson(String url, String json, Map<String, Object> header, Integer timeoutSeconds) {
        try {
            HttpPost httpPost = new HttpPost(url);
            if (timeoutSeconds != null) {
                httpPost.setConfig(createConfig(timeoutSeconds));
            }
            // 创建请求内容
            // new StringEntity(json, ContentType.APPLICATION_JSON, UTF_8, false); // 该方式可能会有不兼容的问题
            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
            httpPost.addHeader("Content-Type", "application/json");
            return doPost(httpPost, header);
        } catch (Exception e) {
            log.error(url, e);
            return null;
        }
    }

    /**
     * 发送post请求 表单数据格式,可传文件
     *
     * @param url
     * @param file
     * @param name
     * @param formData
     * @param header
     * @return
     */
    public static String doPostForm(String url, Map<String, Object> formData, Map<String, Object> header, File file, String name) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            // 相当于<input type="file" name="file"/>
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(StandardCharsets.UTF_8);
            // 以浏览器兼容模式运行，防止文件名乱码。必须
            builder.setMode(HttpMultipartMode.EXTENDED);
            if (file != null) {
                builder.addBinaryBody(name, fileInputStream, ContentType.DEFAULT_BINARY, file.getName());
            }
            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                String key = entry.getKey();
                String obj = entry.getValue().toString();
                // 相当于<input type="text" name="userName" value=userName>
                StringBody value = new StringBody(obj, ContentType.create("text/plain", StandardCharsets.UTF_8));
                builder.addPart(key, value);
            }
            HttpEntity entity = builder.build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            return doPost(httpPost, header);
        } catch (Exception e) {
            log.error(url, e);
            return null;
        }
    }

    /**
     * 发送post请求 表单数据
     *
     * @param url
     * @param param
     * @param header
     * @return
     */
    public static String doPostForm(String url, Map<String, Object> param, Map<String, Object> header, Integer timeoutSeconds) {
        try {
            HttpPost httpPost = new HttpPost(url);
            if (timeoutSeconds != null) {
                httpPost.setConfig(createConfig(timeoutSeconds));
            }
            if (param != null) {
                List<BasicNameValuePair> pairs = param.entrySet().stream()
                        .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue().toString()))
                        .collect(Collectors.toList());
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8));
            }
            return doPost(httpPost, header);
        } catch (Exception e) {
            log.error(url, e);
            return null;
        }
    }

    @SneakyThrows
    private static String doPost(HttpPost httpPost, Map<String, Object> header) {
        if (header != null) {
            for (Map.Entry<String, Object> map : header.entrySet()) {
                httpPost.addHeader(map.getKey(), map.getValue().toString());
            }
        }
        // 执行http请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        // 获取响应对象 EntityUtils.toString()会关闭流且释放连接
        String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return result;
    }

    public static String doGet(String url, Map<String, Object> header, Integer timeoutSeconds) {
        HttpGet httpGet = new HttpGet(url);
        if (timeoutSeconds != null) {
            httpGet.setConfig(createConfig(timeoutSeconds));
        }
        if (MapUtils.isNotEmpty(header)) {
            for (Map.Entry<String, Object> map : header.entrySet()) {
                httpGet.addHeader(map.getKey(), map.getValue().toString());
            }
        }
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String res = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return res;
        } catch (Exception e) {
            log.error(url, e);
            return null;
        }
    }

    public static CloseableHttpClient getClient() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(createRegistry());
        //检测有效链接的间隔
        manager.setValidateAfterInactivity(TimeValue.ofSeconds(90));
        //设定链接池最大数量
        manager.setMaxTotal(1000);
        //设定默认单个路由的最大链接数
        manager.setDefaultMaxPerRoute(800);
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableAutomaticRetries()
                .setConnectionManager(manager)
                .setDefaultRequestConfig(createConfig(3))
                .build();
        return httpClient;
    }

    /**
     * 忽略https证书校验
     *
     * @return
     */
    public static Registry<ConnectionSocketFactory> createRegistry() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", createSSLConnectionFactory())
                .build();
        return registry;
    }

    @SneakyThrows
    public static SSLConnectionSocketFactory createSSLConnectionFactory() {
        SSLContext context = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        return new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
    }

    /**
     * 请求配置
     *
     * @param timeoutSeconds
     * @return
     */
    private static RequestConfig createConfig(int timeoutSeconds) {
        // 必须设置响应超时时间
        Timeout timeout = Timeout.ofSeconds(timeoutSeconds);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setResponseTimeout(timeout)
                .build();
        return config;
    }

}
