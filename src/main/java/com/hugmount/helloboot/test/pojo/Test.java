package com.hugmount.helloboot.test.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试表
 *
 * @Author: Li Huiming
 * @Date: 2019/3/11
 */
@Data
public class Test implements Serializable {

    private static final long serialVersionUID = 2588923803229666442L;

    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 制定时间类型序列化处理类, 否则redis序列化时报错
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creatTime;

    @NotBlank(message = "密码不能为空")
    private String username;

    @Min(value = 6, message = "最小长度6位")
    private String password;

}
