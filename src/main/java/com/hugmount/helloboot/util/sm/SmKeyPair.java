package com.hugmount.helloboot.util.sm;

import lombok.Data;

/**
 * 公钥加密, 私钥解密
 *
 * @author: lhm
 * @date: 2022/9/16
 */
@Data
public class SmKeyPair {

    /**
     * 公钥
     */
    private String pubKey;

    /**
     * 私钥
     */
    private String priKey;

}
