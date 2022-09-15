package com.hugmount.helloboot;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;

import java.io.UnsupportedEncodingException;

/**
 * 国密算法
 * sm2 > rsa
 * sm3 > sha250/md5
 * sm4 > aes
 *
 * @author: lhm
 * @date: 2022/9/15
 */
public class TestSM {

    private static final String key = "1111111122222222";
    private static final String iv = "1111111122222222";

    public static void main(String[] args) throws UnsupportedEncodingException {
        SM4 sm4 = new SM4(Mode.CBC, Padding.PKCS5Padding, key.getBytes("utf-8"), iv.getBytes("utf-8"));
        String lhm = sm4.encryptBase64("lhm您好");
        System.out.println("密文: " + lhm);
        String decryptStr = sm4.decryptStr(lhm);
        System.out.println("解密后: " + decryptStr);
    }

}
