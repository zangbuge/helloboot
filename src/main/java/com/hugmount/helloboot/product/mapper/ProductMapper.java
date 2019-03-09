package com.hugmount.helloboot.product.mapper;

import com.hugmount.helloboot.product.pojo.ProductInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    int insertProduct(ProductInfo productInfo);

    List<ProductInfo> getProductList(ProductInfo productInfo);

}
