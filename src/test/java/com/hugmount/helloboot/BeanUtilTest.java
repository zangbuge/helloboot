package com.hugmount.helloboot;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.hugmount.helloboot.test.pojo.User;

/**
 * @author: lhm
 * @date: 2023/10/11
 */
public class BeanUtilTest {
    public static void main(String[] args) {
        User user = new User();
        user.setUsername("lhm");
        user.setRemark("12");
        User user1 = new User();
        user1.setPassword("123");
        user1.setUsername("LHM");
        // 复制对象属性, 不覆盖原有的值, 设置忽略user1的空值
        BeanUtil.copyProperties(user1, user, CopyOptions.create().setIgnoreNullValue(true));
//        BeanUtil.copyProperties(user1, user);
        System.out.println(JsonUtil.toJson(user));
    }
}
