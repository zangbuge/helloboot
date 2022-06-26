package com.hugmount.helloboot.util;

import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 基于java原生Cipher实现的加密解密工具类
 * ******** key至少8字节 ********
 *
 * @Author Li Huiming
 * @Date 2019/10/26
 */


public class EnCoderCipherUtil {

    /**
     * 加密解密模式(目前使用广泛的对称加密算法有: DES, IDEA, AES 等, AES 即将成为美国国家提倡的标准取代DES )
     */
    private final static String CIPHER_MODE = "DES";


    /**
     * function 加密通用方法
     *
     * @param originalContent:明文
     * @param key                加密秘钥 key至少8字节
     * @return 密文
     */
    public static String encrypt(String originalContent, String key) {
        // 明文或加密秘钥为空时
        if (StringUtils.isEmpty(originalContent) || StringUtils.isEmpty(key)) {
            return null;
        }

        // 明文或加密秘钥不为空时
        try {
            byte[] byteContent = encrypt(originalContent.getBytes(), key.getBytes());
            return new String(Base64.getEncoder().encode(byteContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * function 解密通用方法
     *
     * @param ciphertext 密文
     * @param key        DES解密秘钥（同加密秘钥）
     * @return 明文
     */
    public static String decrypt(String ciphertext, String key) {
        // 密文或加密秘钥为空时
        if (StringUtils.isEmpty(ciphertext) || StringUtils.isEmpty(key)) {
            return null;
        }

        // 密文或加密秘钥不为空时
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bufCiphertext = decoder.decode(ciphertext);
            byte[] contentByte = decrypt(bufCiphertext, key.getBytes());
            return new String(contentByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * function 字节加密方法
     *
     * @param originalContent：明文
     * @param key                加密秘钥的byte数组
     * @return 密文的byte数组
     */
    private static byte[] encrypt(byte[] originalContent, byte[] key) throws Exception {
        // 步骤1：生成可信任的随机数源
        SecureRandom secureRandom = new SecureRandom();

        // 步骤2：基于密钥数据创建DESKeySpec对象
        DESKeySpec desKeySpec = new DESKeySpec(key);

        // 步骤3：创建密钥工厂，将DESKeySpec转换成SecretKey对象来保存对称密钥
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER_MODE);
        SecretKey securekey = keyFactory.generateSecret(desKeySpec);

        // 步骤4：Cipher对象实际完成加密操作,指定其支持指定的加密解密算法
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);

        // 步骤5：用密钥初始化Cipher对象,ENCRYPT_MODE表示加密模式
        cipher.init(Cipher.ENCRYPT_MODE, securekey, secureRandom);

        // 返回密文
        return cipher.doFinal(originalContent);
    }


    /**
     * function 字节解密方法
     *
     * @param ciphertextByte:字节密文
     * @param key                 解密秘钥（同加密秘钥）byte数组
     * @return 明文byte数组
     */
    private static byte[] decrypt(byte[] ciphertextByte, byte[] key) throws Exception {
        // 步骤1：生成可信任的随机数源
        SecureRandom secureRandom = new SecureRandom();

        // 步骤2：从原始密钥数据创建DESKeySpec对象
        DESKeySpec desKeySpec = new DESKeySpec(key);

        // 步骤3：创建密钥工厂，将DESKeySpec转换成SecretKey对象来保存对称密钥
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER_MODE);
        SecretKey securekey = keyFactory.generateSecret(desKeySpec);

        // 步骤4：Cipher对象实际完成解密操作,指定其支持响应的加密解密算法
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);

        // 步骤5：用密钥初始化Cipher对象，DECRYPT_MODE表示解密模式
        cipher.init(Cipher.DECRYPT_MODE, securekey, secureRandom);

        // 返回明文
        return cipher.doFinal(ciphertextByte);
    }


}
