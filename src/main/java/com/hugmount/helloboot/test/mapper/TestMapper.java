package com.hugmount.helloboot.test.mapper;

import com.hugmount.helloboot.test.pojo.Test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestMapper {

    List<Test> getTestList(Test test);

    Integer insertTest(Test test);
}
