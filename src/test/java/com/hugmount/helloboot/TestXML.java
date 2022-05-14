package com.hugmount.helloboot;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.test.pojo.User;
import com.hugmount.helloboot.util.XMLUtil;

/**
 * @author Li Huiming
 * @date 2022/5/14
 */
public class TestXML {
    public static void main(String[] args) {
        User user = new User();
        user.setUsername("lhm");
        String s = XMLUtil.convertObjToXml(user);
        System.out.println(s);
        Object object = XMLUtil.convertXmlToObject(s, User.class);
        System.out.println(JSON.toJSONString(object));
    }
}
