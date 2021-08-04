package com.hugmount.helloboot.test.pojo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 测试表
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */
@Data
public class Test implements Serializable {

    private static final long serialVersionUID = 2588923803229666442L;

    private Long id;

    private LocalDateTime creatTime;

}
