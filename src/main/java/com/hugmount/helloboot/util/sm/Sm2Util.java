package com.hugmount.helloboot.util.sm;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.security.PublicKey;

/**
 * @author: lhm
 * @date: 2022/9/16
 */
public class Sm2Util {

    public static SmKeyPair genKeyPair() {
        SM2 sm2 = SmUtil.sm2();
        //这里会自动生成对应的随机秘钥对
        byte[] privateKey = BCUtil.encodeECPrivateKey(sm2.getPrivateKey());
        //这里默认公钥压缩  公钥的第一个字节用于表示是否压缩 02或者03表示是压缩公钥,04表示未压缩公钥
        byte[] publicKey = BCUtil.encodeECPublicKey(sm2.getPublicKey());
        String priKey = HexUtil.encodeHexStr(privateKey);
        String pubKey = HexUtil.encodeHexStr(publicKey);
        SmKeyPair smKeyPair = new SmKeyPair();
        smKeyPair.setPubKey(pubKey);
        smKeyPair.setPriKey(priKey);
        return smKeyPair;
    }

    /**
     * 公钥加密
     *
     * @param publicKey
     * @param text
     * @return
     */
    public static String encrypt(String publicKey, String text) {
        PublicKey key = BCUtil.decodeECPoint(publicKey, SmUtil.SM2_CURVE_NAME);
        ECPublicKeyParameters ecPublicKeyParameters = BCUtil.toParams(key);
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);
        // 公钥加密
        return sm2.encryptBcd(text, KeyType.PublicKey);
    }

    /**
     * 私钥解密
     *
     * @param privateKey
     * @param cipherData
     * @return
     */
    public static String decrypt(String privateKey, String cipherData) {
        ECPrivateKeyParameters ecPrivateKeyParameters = BCUtil.toSm2Params(privateKey);
        SM2 sm2 = new SM2(ecPrivateKeyParameters, null);
        // 私钥解密
        return StrUtil.utf8Str(sm2.decryptFromBcd(cipherData, KeyType.PrivateKey));
    }


    /**
     * 签名, 使用SM2曲线点构建SM2, 入参都是16进制
     * 文档: https://www.hutool.cn/docs/#/crypto/国密算法工具-SmUtil
     *
     * @param privateKeyHex
     * @param x
     * @param y
     * @param id
     * @param data
     * @return
     */
    public static String signHex(String privateKeyHex, String x, String y, String id, String data) {
        SM2 sm2 = new SM2(privateKeyHex, x, y);
        // 生成的签名是64位
        sm2.usePlainEncoding();
        return sm2.signHex(data, id);
    }

}
