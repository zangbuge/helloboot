package com.hugmount.helloboot.product.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@Data //免去写setter ,getter方法
@ApiModel(description = "产品详情")  //在线API
@TableName("product_info")
public class ProductInfo implements Serializable{

    private static final long serialVersionUID = 5232564460048522959L;

    private String productId;

    @ApiModelProperty(value = "产品名称",hidden = false) //在线API
    @NotNull
    private String productName;

    @ApiModelProperty(value = "产品价格",hidden = false) //在线API
    private Double productPrice;

    private Integer productStock;

    private String productDescription;

    private String productIcon;

    private Integer productStatus;

    private Integer categoryType;

    private Date createTime;

    private Date updateTime;

}
