package com.hugmount.helloboot.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.DES;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 基于Hutool工具类的加密解密类
 *
 * @Author Li Huiming
 * @Date 2019/10/26
 */


public class EnCoderHutoolUtil {

    // 构建RSA对象
    private static RSA rsa = new RSA();
    // 获得私钥
    private static PrivateKey privateKey = rsa.getPrivateKey();
    // 获得公钥
    private static PublicKey publicKey = rsa.getPublicKey();


    /**
     * function RSA加密通用方法:对称加密解密
     *
     * @param originalContent:明文
     * @return 密文
     */
    public static String rsaEncrypt(String originalContent) {
        // 明文或加密秘钥为空时
        if (StringUtils.isEmpty(originalContent)) {
            return null;
        }

        // 公钥加密，之后私钥解密
        return rsa.encryptBase64(originalContent, KeyType.PublicKey);
    }


    /**
     * function RSA解密通用方法:对称加密解密
     *
     * @param ciphertext 密文
     * @return 明文
     */
    public static String rsaDecrypt(String ciphertext) {
        // 密文或加密秘钥为空时
        if (StringUtils.isEmpty(ciphertext)) {
            return null;
        }

        return rsa.decryptStr(ciphertext, KeyType.PrivateKey);
    }


    /**
     * function DES加密通用方法:对称加密解密
     *
     * @param originalContent:明文
     * @param key                加密秘钥
     * @return 密文
     */
    public static String desEncrypt(String originalContent, String key) {
        // 明文或加密秘钥为空时
        if (StringUtils.isEmpty(originalContent) || StringUtils.isEmpty(key)) {
            return null;
        }

        // 还可以生成随机生成密钥
        // byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();

        // 构建
        DES des = SecureUtil.des(key.getBytes());

        // 加密
        return des.encryptHex(originalContent);

    }


    /**
     * function DES解密通用方法:对称加密解密
     *
     * @param ciphertext 密文
     * @param key        DES解密秘钥（同加密秘钥）
     * @return 明文
     */
    public static String desDecrypt(String ciphertext, String key) {
        // 密文或加密秘钥为空时
        if (StringUtils.isEmpty(ciphertext) || StringUtils.isEmpty(key)) {
            return null;
        }

        // 还可以生成随机生成密钥
        // byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();

        // 构建
        DES des = SecureUtil.des(key.getBytes());
        // 解密

        return des.decryptStr(ciphertext);
    }


}
