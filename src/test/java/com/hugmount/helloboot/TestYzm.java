package com.hugmount.helloboot;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;

/**
 * @author: lhm
 * @date: 2023/1/9
 */
public class TestYzm {
    public static void main(String[] args) {
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

        //图形验证码写出，可以写出到文件，也可以写出到流
        lineCaptcha.write("d:/line.png");
        //输出code
        System.out.println("默认随机5位验证码: " + lineCaptcha.getCode());
        //验证图形验证码的有效性，返回boolean值
        lineCaptcha.verify("1234");

        //重新生成验证码
        lineCaptcha.createCode();
        lineCaptcha.write("d:/line.png");
        //新的验证码
        System.out.println("新二维码: " + lineCaptcha.getCode());
        //验证图形验证码的有效性，返回boolean值
        lineCaptcha.verify("1234");

        // 圆形干扰验证码
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 150);
        captcha.write("d:/line4.png");
        System.out.println("自定义4位验证码: " + captcha.getCode());

        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 18);
        shearCaptcha.write("d:/line1.png");
        System.out.println("扭曲干扰验证码: " + shearCaptcha.getCode());
    }
}
