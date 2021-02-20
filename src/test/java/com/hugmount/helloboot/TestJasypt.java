package com.hugmount.helloboot;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Li Huiming
 * @Date: 2021/2/20
 */
public class TestJasypt extends HellobootApplicationTests{

    @Autowired
    private StringEncryptor stringEncryptor;

    @Test
    public void test() {
        // 每次加密过程中都要用到随机的元素,最后生成的密文就不一样。
        String encrypt = stringEncryptor.encrypt("root");
        System.out.println("ENC密文: " + encrypt);

        String decrypt = stringEncryptor.decrypt(encrypt);
        System.out.println("ENC解密后明文: " + decrypt);
        String str = "6W+3BwgLsHDvCqB9+uXT108pKYJAleAYRSOx+GkI0exqCCqJsZsxDDTCmGcvbw97";

        System.out.println(stringEncryptor.decrypt(str));

        /**
         * 加密数据库密码
         * 一. yml中添加配置盐值
         * jasypt.encryptor.password=salt
         * 二. 配置密文
         * spring.datasource.password=ENC(6W+3BwgLsHDvCqB9+uXT108pKYJAleAYRSOx+GkI0exqCCqJsZsxDDTCmGcvbw97)
         * 三. springboot启动类开启注解
         * @EnableEncryptableProperties
         *
         */

    }
}
