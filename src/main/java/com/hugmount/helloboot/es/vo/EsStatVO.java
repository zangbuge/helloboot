package com.hugmount.helloboot.es.vo;

import lombok.Data;

/**
 * @author lhm
 * @date 2024/4/16
 */
@Data
public class EsStatVO {

    /**
     * 分组id
     */
    private String groupId;

    /**
     * 统计数量
     */
    private int cnt;

    /**
     * 分页 last
     */
    private Object[] objects;

}
