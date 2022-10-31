package com.hugmount.helloboot;

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


    }
}
