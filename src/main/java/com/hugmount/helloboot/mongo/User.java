package com.hugmount.helloboot.mongo;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Map;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/16
 */
@Data
@Entity(value = "user")
public class User {
    @Id
    private String id;
    private String name;
    private String addr;
    private Map<String, Object> child;
}
