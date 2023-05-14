package com.hugmount.helloboot.util;

import org.springframework.util.ObjectUtils;

/**
 * 脱敏工具
 *
 * @author: lhm
 * @date: 2023/5/6
 */
public class DesensitizeUtil {

    public static String desensitizeName(String name) {
        if (name.length() > 3) {
            return desensitize(name, 0, 2);
        }
        return desensitize(name, 0, 1);
    }

    public static String desensitizePhone(String phone) {
        return desensitize(phone, 3, 4);
    }

    public static String desensitizeCredentialNo(String credentialNo) {
        return desensitize(credentialNo, 6, 4);
    }

    public static String desensitize(String text, int leftKeep, int rightKeep) {
        if (ObjectUtils.isEmpty(text)) {
            return text;
        }
        int length = text.length();
        int keep = leftKeep + rightKeep;
        if (keep >= length) {
            return text;
        }
        String left = text.substring(0, leftKeep);
        String right = text.substring(length - rightKeep, length);
        return left.concat(genStarStr(length - keep)).concat(right);
    }

    private static String genStarStr(int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append("*");
        }
        return builder.toString();
    }

}
