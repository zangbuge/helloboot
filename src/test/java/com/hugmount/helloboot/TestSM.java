package com.hugmount.helloboot;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.symmetric.SM4;
import com.hugmount.helloboot.util.sm.Sm2Util;
import com.hugmount.helloboot.util.sm.SmKeyPair;
import org.apache.commons.lang3.RandomStringUtils;
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
        System.out.println("sm2签名16进制(每次签名都不一样): " + sign);
        boolean verifyHex = Sm2Util.verifyHex(privateKeyHex, x, y, id, data, sign);
        System.out.println("验签结果: " + verifyHex);

        String content = "lhm";
        String sm2SignKey = "123456";
        final SM2 sm2 = SmUtil.sm2();
        String sm2Sign = sm2.signHex(HexUtil.encodeHexStr(content), sm2SignKey);
        System.out.println("sm2Sign: " + sm2Sign);
        boolean verify = sm2.verifyHex(HexUtil.encodeHexStr(content), sm2Sign, sm2SignKey);
        System.out.println("verify: " + verify);

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

        String s = HexUtil.encodeHexStr(RandomStringUtils.randomNumeric(16));
        byte[] b = HexUtil.decodeHex(s);
        String b64 = Base64.getUrlEncoder().encodeToString(b);
        System.out.println("随机16位秘钥(16进制base64编码的key): " + b64);

        // 16进制base64编码的key
        String str = "lhm";
        String k = "NjE0Mzc3Nzk3NzMwMjY0Mg==";
        String v = "NzM1ODc1NzkyMjM0MDE0MQ==";
        SM4 sm = new SM4(Mode.CBC, Padding.PKCS5Padding, Base64.getDecoder().decode(k), Base64.getDecoder().decode(v));
        String s1 = sm.encryptBase64(str);
        System.out.println("加密: " + s1);
        System.out.println("解密: " + sm.decryptStr(s1));
    }

}
