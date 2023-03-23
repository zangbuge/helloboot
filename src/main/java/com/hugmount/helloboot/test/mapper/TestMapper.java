package com.hugmount.helloboot.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hugmount.helloboot.test.pojo.Test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestMapper extends BaseMapper<Test> {

    List<Test> getTestList(Test test);

    Integer insertTest(Test test);
}
