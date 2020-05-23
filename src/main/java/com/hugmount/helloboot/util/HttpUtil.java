package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
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
     * @param url     请求链接
     * @param params  map类型的参数
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

            if (null != headerMap) {
                Iterator<Entry<String, Object>> iterator = headerMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> next = iterator.next();
                    conn.setRequestProperty(next.getKey().toString(), next.getValue().toString());
                }
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //默认编码格式
            String charset = "UTF-8";
            // 获取URLConnection对象对应的输出流
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), charset);
            if (sbParams != null && sbParams.length() > 0) {
                // 设置请求参数
                writer.write(sbParams.substring(0, sbParams.length() - 1));
                // flush输出流的缓冲
                writer.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
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
     * 使用HttpURLConnection下载文件
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

        HttpURLConnection con = null;
        BufferedReader br = null;
        FileOutputStream os = null;
        try {
            URL url = null;
            if (ParamsStr != null && ParamsStr.length() > 0) {
                urlParam = urlParam + "?" + ParamsStr.substring(0, ParamsStr.length() - 1);
            }
            url = new URL(urlParam);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.connect();

            InputStream is = con.getInputStream();
            os = new FileOutputStream(fileSavePath);
            byte buf[] = new byte[1024];
            int count = 0;
            while ((count = is.read(buf)) != -1) {
                os.write(buf, 0, count);
            }
            os.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    os = null;
                    throw new RuntimeException(e);
                } finally {
                    if (con != null) {
                        con.disconnect();
                        con = null;
                    }
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                    throw new RuntimeException(e);
                } finally {
                    if (con != null) {
                        con.disconnect();
                        con = null;
                    }
                }
            }
        }

    }


    /**
     * 处理josn格式的post请求
     *
     * @param urlSource
     * @param json
     * @return
     */
    public static Map<String, String> doJosnPost(String urlSource, String json, Map<String, Object> headerMap) throws Exception {
        log.error("======doJosnPost=====：" + urlSource);
        Map<String, String> map = new HashMap<String, String>();
        OutputStream out = null;
        InputStream in = null;
        try {
            log.error("请求地址：" + urlSource);
            log.error("请求参数：" + json);
            URL requestUrl = new URL(urlSource);
            HttpURLConnection urlConnect = (HttpURLConnection) requestUrl.openConnection();
            urlConnect.setRequestMethod("POST");//设定请求的方法为"POST"，默认是GET
            urlConnect.setDoOutput(true);
            urlConnect.setDoInput(true);//设置是否向httpUrlConnection输入
            urlConnect.setUseCaches(false);//Post 请求不能使用缓存
            urlConnect.setRequestProperty("Content-type", "application/json;charset=utf-8");
            Iterator<Entry<String, Object>> iterator = headerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> next = iterator.next();
                urlConnect.setRequestProperty(next.getKey().toString(), next.getValue().toString());
            }
            out = urlConnect.getOutputStream();
            out.write(json.getBytes("UTF-8"));
            out.flush();
            urlConnect.connect();
            if (urlConnect.getResponseCode() == 200) {
                in = urlConnect.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuffer stringBuffer = new StringBuffer();
                String s = null;
                while ((s = reader.readLine()) != null) {
                    stringBuffer.append(s);
                }
                map.put("resultCode", "200");
                map.put("message", urlConnect.getResponseMessage());
                map.put("responseXml", stringBuffer.toString());
            } else {
                map.put("resultCode", urlConnect.getResponseCode() + "");
                map.put("message", urlConnect.getResponseMessage());
            }
        } catch (Exception e) {
            log.error("httpUtils.doJosnPost 异常{}, {}:", e.getMessage(), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException oe) {
                log.error("流关闭异常");
            }
        }
        return map;
    }

}
