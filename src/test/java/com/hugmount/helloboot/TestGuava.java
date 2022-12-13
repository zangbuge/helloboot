package com.hugmount.helloboot;

import com.hugmount.helloboot.util.LocalCacheUtil;

/**
 * @author: lhm
 * @date: 2022/12/13
 */
public class TestGuava {
    public static void main(String[] args) {
        LocalCacheUtil.put("lhm", "hello");
        System.out.println(LocalCacheUtil.get("lhm"));
        LocalCacheUtil.delete("lhm");
    }
}
