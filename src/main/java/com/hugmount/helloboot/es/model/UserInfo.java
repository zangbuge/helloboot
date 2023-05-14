package com.hugmount.helloboot.es.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: lhm
 * @date: 2022/8/2
 */
@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 944153747312207298L;

    private String id;

    /**
     * 1，ik_max_word：会对文本做最细 力度的拆分
     * 2，ik_smart：会对文本做最粗粒度的拆分
     */
    private String name;

    private String addr;

    private Integer age;

    private Date date;


}
