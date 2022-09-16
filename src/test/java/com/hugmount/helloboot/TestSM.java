package com.hugmount.helloboot;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.symmetric.SM4;
import com.hugmount.helloboot.util.sm.Sm2Util;
import com.hugmount.helloboot.util.sm.SmKeyPair;
import org.junit.Test;

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

}
