package com.hugmount.helloboot.dict.model;

import lombok.Data;

import java.util.Date;

@Data
public class DictConfig {

    private String id;

    private String pid;

    private String groupCode;

    private String groupName;

    private String dictKey;

    private String dictVal;

    private Integer dictSort;

    private String status;

    private String remark;

    private String deleted;

    private Date createTime;

    private Date updateTime;

}