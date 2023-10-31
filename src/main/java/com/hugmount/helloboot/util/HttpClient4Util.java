package com.hugmount.helloboot.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Li Huiming
 * @Date 2019/9/19
 */

@Slf4j
public class HttpClient4Util {

    private HttpClient4Util() {
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
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
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
            Optional.ofNullable(timeoutSeconds).ifPresent(it -> httpPost.setConfig(createConfig(timeoutSeconds)));
            Optional.ofNullable(param).ifPresent(item -> {
                List<BasicNameValuePair> pairs = param.entrySet().stream()
                        .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue().toString()))
                        .collect(Collectors.toList());
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8));
            });
            return doPost(httpPost, header);
        } catch (Exception e) {
            log.error(url, e);
            return null;
        }
    }

    @SneakyThrows
    private static String doPost(HttpPost httpPost, Map<String, Object> header) {
        Optional.ofNullable(header).ifPresent(item -> {
            for (Map.Entry<String, Object> map : header.entrySet()) {
                httpPost.addHeader(map.getKey(), map.getValue().toString());
            }
        });
        CloseableHttpResponse response = httpClient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }

    public static String doGet(String url, Map<String, Object> header, Integer timeoutSeconds) {
        HttpGet httpGet = new HttpGet(url);
        Optional.ofNullable(timeoutSeconds).ifPresent(item -> httpGet.setConfig(createConfig(timeoutSeconds)));
        Optional.ofNullable(header).ifPresent(item -> {
            for (Map.Entry<String, Object> map : header.entrySet()) {
                httpGet.addHeader(map.getKey(), map.getValue().toString());
            }
        });
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
        SSLConnectionSocketFactory sslConnectionFactory = createSSLConnectionFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslConnectionFactory)
                .build();
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);
        //检测有效链接的间隔
        manager.setValidateAfterInactivity(90 * 1000);
        //设定链接池最大数量
        manager.setMaxTotal(500);
        //设定默认单个路由的最大链接数
        manager.setDefaultMaxPerRoute(500);
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableAutomaticRetries()
                .setConnectionManager(manager)
                .setDefaultRequestConfig(createConfig(3))
                .build();

        return httpClient;
    }

    /**
     * 请求配置
     *
     * @param timeoutSeconds
     * @return
     */
    private static RequestConfig createConfig(int timeoutSeconds) {
        int timeout = timeoutSeconds * 1000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        return config;
    }

    /**
     * 忽略https证书的连接
     *
     * @return
     */
    @SneakyThrows
    public static SSLConnectionSocketFactory createSSLConnectionFactory() {
        SSLContext context = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        return new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
    }

}
