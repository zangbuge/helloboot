package com.hugmount.helloboot.util;

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
import java.util.Map;
import java.util.Map.Entry;

/**http请求工具类
 * @author Li Huiming
 *
 * @date 2018年10月18日
 */
public class HttpUtil {


	public static String sendPost(String url, String jsonParam){
		HttpURLConnection httpURLConnection = null;
		OutputStream out = null; //写
		int responseCode = 0;    //远程主机响应的HTTP状态码
		String result = "";
		try{
			URL sendUrl = new URL(url);
			httpURLConnection = (HttpURLConnection)sendUrl.openConnection();
			//post方式请求
			httpURLConnection.setRequestMethod("POST");
			//设置头部信息
			httpURLConnection.setRequestProperty("headerdata", "ceshiyongde");
			//一定要设置 Content-Type 要不然服务端接收不到参数
			httpURLConnection.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
			//指示应用程序要将数据写入URL连接,其值默认为false（是否传参）
			httpURLConnection.setDoOutput(true);

			httpURLConnection.setUseCaches(false);
			httpURLConnection.setConnectTimeout(30000); //30秒连接超时
			httpURLConnection.setReadTimeout(30000);    //30秒读取超时
			//传入参数
			out = httpURLConnection.getOutputStream();
			out.write(jsonParam.getBytes());
			out.flush(); //清空缓冲区,发送数据
			out.close();
			responseCode = httpURLConnection.getResponseCode();
			//获取请求的资源
			BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
			result = br.readLine();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("远程主机响应的HTTP状态码: " + responseCode);
		return result;

	}

	/**使用URLConnection发送post
	 * @param url 请求链接
	 * @param params map类型的参数
	 * @param charset 编码值为null: default UTF-8
	 * @return 请求响应结果
	 */
	public static String sendPost(String url, Map<String, Object> params, String charset) {
		StringBuffer resultBuffer = null;
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
		URLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			con = realUrl.openConnection();
			// 设置通用的请求属性
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			con.setDoOutput(true);
			con.setDoInput(true);
			//默认编码格式
			if(null == charset || "" == charset)
				charset = "UTF-8";
			// 获取URLConnection对象对应的输出流
			osw = new OutputStreamWriter(con.getOutputStream(), charset);
			if (sbParams != null && sbParams.length() > 0) {
				// 发送请求参数
				osw.write(sbParams.substring(0, sbParams.length() - 1));
				// flush输出流的缓冲
				osw.flush();
			}
			// 定义BufferedReader输入流来读取URL的响应
			resultBuffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					osw = null;
					throw new RuntimeException(e);
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
					throw new RuntimeException(e);
				}
			}
		}

		return resultBuffer.toString();
	}



	/** 使用HttpURLConnection下载文件
	 * @param urlParam 
	 * @param params
	 * @param fileSavePath 设置保存文件的路径
	 */
	public static void sendGetForDownloadFile(String urlParam, Map<String, Object> params, String fileSavePath) {
		// 构建请求参数
		StringBuffer ParamsStr = new StringBuffer();
		if(params != null && params.size() > 0) {
			for(Entry<String, Object> entry : params.entrySet()) {
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
			if(ParamsStr != null && ParamsStr.length() > 0) {
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
			if(os != null) {
				try {
					os.close();
				} catch (IOException e) {
					os = null;
					throw new RuntimeException(e);
				} finally {
					if(con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
					throw new RuntimeException(e);
				} finally {
					if(con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
		}

	}




}
