package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * http请求工具类
 *
 * @author Li Huiming
 * @date 2018年10月18日
 */
@Slf4j
public class HttpUtil {

    public static final String UTF_8 = "UTF-8";
    public static final String JSON_TYPE = "application/json; charset=UTF-8";
    public static final String FORM_TYPE = "application/x-www-form-urlencoded";

    public static String sendPostJson(String url, String jsonParam, Map<String, Object> header) {
        return sendPost(url, jsonParam, header, JSON_TYPE, null);
    }

    public static String sendPostForm(String url, Map<String, Object> formData, Map<String, Object> header) {
        String data = assembleFormData(formData);
        return sendPost(url, data, header, FORM_TYPE, null);
    }

    public static String downloadFile(String url, Map<String, Object> formData, Map<String, Object> header, String downloadFile) {
        String data = assembleFormData(formData);
        return sendPost(url, data, header, FORM_TYPE, downloadFile);
    }

    private static String assembleFormData(Map<String, Object> formData) {
        if (formData == null) {
            return null;
        }
        // 构建请求参数
        StringBuffer param = new StringBuffer();
        if (formData != null && formData.size() > 0) {
            for (Entry<String, Object> entry : formData.entrySet()) {
                param.append(entry.getKey());
                param.append("=");
                param.append(entry.getValue());
                param.append("&");
            }
        }
        String str = param.toString();
        return str.substring(0, str.length() - 1);
    }

    private static String sendPost(String url, String jsonParam, Map<String, Object> header, String type, String downloadFile) {
        URLConnection urlConnection;
        try {
            URL callUrl = new URL(url);
            urlConnection = callUrl.openConnection();
        } catch (IOException e) {
            throw new RuntimeException("获取URLConnection对象异常", e);
        }
        if (null != header) {
            Iterator<Entry<String, Object>> iterator = header.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> next = iterator.next();
                urlConnection.setRequestProperty(next.getKey(), next.getValue().toString());
            }
        }
        //一定要设置 Content-Type 要不然服务端接收不到参数
        urlConnection.setRequestProperty("Content-Type", type);
        //setDoOutput支持将数据写入URL连接,POST请求就可以使用conn.getOutputStream().write()传参. 其值默认为false
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setConnectTimeout(30000);
        urlConnection.setReadTimeout(60000);

        if (jsonParam != null) {
            try (OutputStream out = urlConnection.getOutputStream()) {
                //writer传入参数
                out.write(jsonParam.getBytes(UTF_8));
                out.flush();
            } catch (Exception e) {
                throw new RuntimeException("URLConnection建立连接并设置参数异常", e);
            }
        }

        // 下载文件
        if (downloadFile != null) {
            try (InputStream inputStream = urlConnection.getInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(downloadFile)) {

                byte[] buffer = new byte[1024];
                int cnt;
                while ((cnt = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, cnt);
                }
                fileOutputStream.flush();
                return "SUCCESS";
            } catch (IOException e) {
                throw new RuntimeException("URLConnection下载文件异常", e);
            }
        }

        // 获取请求响应数据
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException("URLConnection获取响应异常", e);
        }
    }

}
