package com.hugmount.helloboot;

import cn.hutool.core.net.url.UrlBuilder;

import java.nio.charset.Charset;

/**
 * @Author: Li Huiming
 * @Date: 2021/3/22
 */
public class TestUrlBuilder {
    public static void main(String[] args) {
        System.out.println(213);
        UrlBuilder isShared = UrlBuilder.ofHttp("http://10.10.10.10:8080/test", Charset.defaultCharset())
                .addQuery("name", "1")
                .addQuery("hello", "1")
                .addQuery("adde", "1");
        System.out.println("url :" + isShared.build());
        String url = "http://10.10.10.10:8080/test?name=1&hello=1";
        int hello = url.indexOf("hello");
        System.out.println(hello);
        String isShared1 = url.substring(0, url.indexOf("hello"));
        System.out.println(isShared1);
    }
}
