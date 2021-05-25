package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * http请求工具类
 *
 * @author Li Huiming
 * @date 2018年10月18日
 */
@Slf4j
public class HttpUtil {

    public static final String UTF_8 = "UTF-8";

    public static String sendPost(String url, String jsonParam, Map<String, Object> header) {

        try {
            URL sendUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) sendUrl.openConnection();
            //post方式请求
            httpURLConnection.setRequestMethod("POST");
            //设置头部信息
            if (null != header) {
                Iterator<Entry<String, Object>> iterator = header.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> next = iterator.next();
                    httpURLConnection.setRequestProperty(next.getKey(), next.getValue().toString());
                }
            }
            //一定要设置 Content-Type 要不然服务端接收不到参数
            httpURLConnection.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
            //指示应用程序要将数据写入URL连接,其值默认为false（是否传参）
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(30000); //30秒连接超时
            httpURLConnection.setReadTimeout(30000);    //30秒读取超时
            //传入参数
            OutputStream out = httpURLConnection.getOutputStream();
            out.write(jsonParam.getBytes(UTF_8));
            //获取请求的资源
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 使用URLConnection发送post
     *
     * @param url    请求链接
     * @param params map类型的参数
     * @return 请求响应结果
     */
    public static String sendPost(String url, Map<String, Object> params, Map<String, Object> headerMap) {
        // 构建请求参数
        StringBuffer sbParams = new StringBuffer();
        if (params != null && params.size() > 0) {
            for (Entry<String, Object> entry : params.entrySet()) {
                sbParams.append(entry.getKey());
                sbParams.append("=");
                sbParams.append(entry.getValue());
                sbParams.append("&");
            }
        }
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            Optional.ofNullable(headerMap).ifPresent(head -> {
                Iterator<Entry<String, Object>> iterator = head.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> next = iterator.next();
                    conn.setRequestProperty(next.getKey(), next.getValue().toString());
                }
            });
            // Post请求往往需要向服务器端发送数据参数，所以需要setDoInput(true)
            // 设置了就可以调用getOutputStream()方法从服务器端获得字节输出流
            conn.setDoOutput(true);
            // 获取URLConnection对象对应的输出流
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), UTF_8);
            if (sbParams != null && sbParams.length() > 0) {
                // 设置请求参数
                writer.write(sbParams.substring(0, sbParams.length() - 1));
                // flush输出流的缓冲
                writer.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF_8));
            StringBuffer resultBuffer = new StringBuffer();
            String temp;
            while ((temp = reader.readLine()) != null) {
                resultBuffer.append(temp);
            }
            return resultBuffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 使用 HttpURLConnection 下载文件
     *
     * @param urlParam
     * @param params
     * @param fileSavePath 设置保存文件的路径
     */
    public static void sendGetForDownloadFile(String urlParam, Map<String, Object> params, String fileSavePath) {
        // 构建请求参数
        StringBuffer ParamsStr = new StringBuffer();
        if (params != null && params.size() > 0) {
            for (Entry<String, Object> entry : params.entrySet()) {
                ParamsStr.append(entry.getKey());
                ParamsStr.append("=");
                ParamsStr.append(entry.getValue());
                ParamsStr.append("&");
            }
        }

        try {
            if (ParamsStr != null && ParamsStr.length() > 0) {
                urlParam = urlParam + "?" + ParamsStr.substring(0, ParamsStr.length() - 1);
            }
            URL url = new URL(urlParam);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(fileSavePath);
            byte buf[] = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, count);
            }
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
