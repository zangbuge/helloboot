package com.hugmount.helloboot.mongo;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;

/**
 * @Author: Li Huiming
 * @Date: 2020/12/16
 */
@Data
@Entity(value = "user")
public class User {
    private String id;
    private String name;
}
