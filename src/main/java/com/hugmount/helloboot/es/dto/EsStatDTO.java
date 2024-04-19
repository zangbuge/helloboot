package com.hugmount.helloboot.es.dto;

import lombok.Data;

import java.util.List;

/**
 * @author lhm
 * @date 2024/4/16
 */
@Data
public class EsStatDTO {

    private Integer pageSize;

    /**
     * 最小个数
     */
    private Long minDocCount;

    /**
     * 开始时间 13位时间戳
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 条件
     */
    private List<String> typeCodeList;

    /**
     * 分页 last
     */
    private Object[] objects;

    private Integer top;

}
