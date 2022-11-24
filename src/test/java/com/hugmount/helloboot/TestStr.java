package com.hugmount.helloboot;

import com.google.common.base.Strings;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Li Huiming
 * @date 2022/4/20
 */
public class TestStr {
    public static void main(String[] args) {
        int youNumber = 126;
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        String str = String.format("%04d", youNumber);
        System.out.println(str); // 0001

        // 字符串左侧补0
        String name = "lhm";
        name = String.format("%6s", name);
        System.out.println(name.replace(" ", "0"));

        String tm = "1号机组增加外置式";
        String desensitize = desensitize(tm, 0.45);
        System.out.println(desensitize);

        System.out.println("随机数: " + RandomStringUtils.randomNumeric(6));

    }

    /**
     * 脱敏
     *
     * @param text
     * @param showScale
     * @return
     */
    public static String desensitize(String text, double showScale) {
        int length = text.length();
        if (length > 12) {
            String sub = text.substring(0, 6);
            String repeat = Strings.repeat("*", length - 6);
            return sub + repeat;
        }
        int show = (int) (length * showScale);
        String substring = text.substring(0, show);
        return substring + Strings.repeat("*", length - show);
    }

}
