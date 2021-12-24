package com.hugmount.helloboot.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 类名：SignatureUtil
 * 功能：签名工具类
 */
public class SignatureUtil {

    public static void main(String[] args) {
        System.out.println(sign("123", ""));
    }

    /**
     * MD5签名
     *
     * @param content 待签名数据
     * @param salt    盐值
     * @return sign
     */
    public static String sign(String content, String salt) {
        //sha系列签名,请看源码
        String sign = DigestUtils.md5Hex(content + salt);
        return sign;
    }

    /**
     * 验签
     *
     * @param content 待签名数据
     * @param sign    签名值
     * @param salt    盐值
     * @return sign
     */
    public static boolean verify(String sign, String content, String salt) {
        String signCur = DigestUtils.md5Hex(content + salt);
        return signCur.equals(sign);
    }


    public static String SHA256Sign(String content, String salt) {
        String sign = DigestUtils.sha256Hex(content + salt);
        return sign;
    }

}