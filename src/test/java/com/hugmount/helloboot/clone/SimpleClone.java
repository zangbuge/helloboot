package com.hugmount.helloboot.clone;

import com.hugmount.helloboot.test.pojo.User;
import lombok.Data;

/**
 * @author lhm
 * @date 2024/7/9
 */

@Data
public class SimpleClone implements Cloneable {

    private int age;

    private User user;

    /**
     * 默认拷贝
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
