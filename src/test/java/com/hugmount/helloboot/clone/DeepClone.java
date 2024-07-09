package com.hugmount.helloboot.clone;

import com.hugmount.helloboot.test.pojo.User;
import lombok.Data;

/**
 * @author lhm
 * @date 2024/7/9
 */
@Data
public class DeepClone implements Cloneable {

    private int age;

    private User user;

    /**
     * 深拷贝
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        DeepClone clone = (DeepClone) super.clone();
        Object clone1 = this.user.clone();
        clone.setUser((User) clone1);
        return clone;
    }

}
