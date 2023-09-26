package com.hugmount.helloboot.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hugmount.helloboot.test.pojo.TTest;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lhm
 * @since 2023-09-26
 */
@Mapper
public interface TTestMapper extends BaseMapper<TTest> {

}
