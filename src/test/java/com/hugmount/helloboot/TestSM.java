package com.hugmount.helloboot;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.symmetric.SM4;
import com.hugmount.helloboot.util.sm.Sm2Util;
import com.hugmount.helloboot.util.sm.SmKeyPair;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

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

    @Test
    public void testSM4() throws UnsupportedEncodingException {
        SM4 sm4 = new SM4(Mode.CBC, Padding.PKCS5Padding, key.getBytes("utf-8"), iv.getBytes("utf-8"));
        String lhm = sm4.encryptBase64("lhm您好");
        System.out.println("密文: " + lhm);
        String decryptStr = sm4.decryptStr(lhm);
        System.out.println("解密后: " + decryptStr);
    }

    @Test
    public void testSM3() {
        SM3 sm3 = new SM3();
        String s = sm3.digestHex("1234567");
        System.out.println("SM3签名: " + s);
    }

    @Test
    public void testSM2() {
        SmKeyPair smKeyPair = Sm2Util.genKeyPair();
        System.out.println(smKeyPair);
        String lhm = Sm2Util.encrypt(smKeyPair.getPubKey(), "lhm您好");
        System.out.println("sm2加密: " + lhm);
        String decrypt = Sm2Util.decrypt(smKeyPair.getPriKey(), lhm);
        System.out.println("解密: " + decrypt);
    }

    @Test
    public void testSm2Sign() {
        String privateKeyHex = "FAB8BBE670FAE338C9E9382B9FB6485225C11A3ECB84C938F10F20A93B6215F0";
        String x = "9EF573019D9A03B16B0BE44FC8A5B4E8E098F56034C97B312282DD0B4810AFC3";
        String y = "CC759673ED0FC9B9DC7E6FA38F0E2B121E02654BF37EA6B63FAF2A0D6013EADF";
        // 数据和ID此处使用16进制表示
        String data = "434477813974bf58f94bcf760833c2b40f77a5fc360485b0b9ed1bd9682edb45";
        String id = "31323334353637383132333435363738";
        String sign = Sm2Util.signHex(privateKeyHex, x, y, id, data);
        System.out.println("sm2签名16进制: " + sign);

        // 16进制转byte数组
        byte[] bytes = HexUtil.decodeHex(sign);
        String base64 = Base64.getUrlEncoder().encodeToString(bytes);
        System.out.println("16进制转base64: " + base64);
        byte[] bytes1 = Base64.getUrlDecoder().decode(base64);
        // 可指定16进制大小写,默认小写
        String hex = HexUtil.encodeHexStr(bytes1, false);
        System.out.println("base64转16进制: " + hex);

        String lhmHex = HexUtil.encodeHexStr("lhm");
        System.out.println("字符转16进制: " + lhmHex);
        System.out.println("16进制转字符: " + HexUtil.decodeHexStr(lhmHex));
    }

}
