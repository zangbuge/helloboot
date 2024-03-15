package com.hugmount.helloboot.test.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author lhm
 * @since 2023-09-26
 */
@Getter
@Setter
@TableName("t_test")
public class TTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * FieldStrategy.ALWAYS 不忽略空, 覆盖更新
     */
    @TableField(value = "username", updateStrategy = FieldStrategy.ALWAYS)
    private String username;

    @TableField("password")
    private String password;

    @TableField("creat_time")
    private LocalDateTime creatTime;


}
