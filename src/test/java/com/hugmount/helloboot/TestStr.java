package com.hugmount.helloboot;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.IdUtil;
import com.google.common.base.Strings;
import com.hugmount.helloboot.util.DesensitizeUtil;
import com.hugmount.helloboot.util.StrUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

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

        System.out.println(DesensitizeUtil.desensitizeName("李会明"));
        System.out.println(DesensitizeUtil.desensitizeName("上官承诺"));
        System.out.println(DesensitizeUtil.desensitizePhone("15897998616"));
        System.out.println(DesensitizeUtil.desensitizeCredentialNo("420622199402136789"));
    }

    @Test
    public void testGetUrlParam() {
        String url = "www.baidu.com/test?name=lhm&phone=123";
        System.out.println(StrUtil.getUrlParam(url, "phone"));

        String build = UrlBuilder.of("http://127.0.1:8080/helloboot/")
                .addPath("/user/addUser")
                .addQuery("username", "lhm")
                .build();
        System.out.println(build);
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

    @Test
    public void testId() {
/*        return timestamp - this.twepoch << 22   //时间戳部分，占41位 左移22位
                | this.dataCenterId << 17       //数据中心部分占5位，左移17位
                | this.workerId << 12          //机器标识部分占5位，左移10位
                | this.sequence;               //序列号部分占12位*/
        Snowflake snowflake = IdUtil.getSnowflake(2, 1);
        String id = snowflake.nextIdStr();
        System.out.println("雪花id: " + id + " 长度: " + id.length());
    }
}
