package com.hugmount.helloboot.util;

/**
 * @Author Li Huiming
 * @Date 2019/10/26
 */


public class CipherUtilTest {

    public static void main(String[] args) {
        // key至少8字节
        String key = "12345678";
        String content = "hello word";

        String decrypt = EnCoderCipherUtil.encrypt(content, key);
        System.out.println("密文: " + decrypt);
        String encrypt = EnCoderCipherUtil.decrypt(decrypt, key);
        System.out.println("明文: " + encrypt);

        String s = EnCoderHutoolUtil.rsaEncrypt(content);
        System.out.println("rsa密文: " + s);
        String s1 = EnCoderHutoolUtil.rsaDecrypt(s);
        System.out.println("rsa明文: " + s1);


        String s2 = EnCoderHutoolUtil.desEncrypt(content, key);
        System.out.println("des密文: " + s2);
        String s3 = EnCoderHutoolUtil.desDecrypt(s2, key);
        System.out.println("des明文: " + s3);

    }
}
