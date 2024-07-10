package com.hugmount.helloboot.util;

/**
 * @author: lhm
 * @date: 2023/6/8
 */
public class StrUtil {
    public static String getUrlParam(String url, String key) {
        String[] split = url.split("\\?");
        if (split.length < 2) {
            return null;
        }
        for (String str : split) {
            String[] split1 = str.split("&");
            for (String s : split1) {
                String[] split2 = s.split("=");
                if (split2.length != 2) {
                    continue;
                }
                if (key.equals(split2[0])) {
                    return split2[1];
                }
            }
        }
        return null;
    }

    public static String getUrlAddr(String url) {
        String[] split = url.split("\\?");
        return split[0];
    }

}
