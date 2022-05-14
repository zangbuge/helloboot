package com.hugmount.helloboot.test.pojo;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Author Li Huiming
 * @Date 2019/8/17
 */

@Data
@XmlRootElement(name = "xml")
public class User {

    private  String username;

    private String password;

}
