package com.hugmount.helloboot.product.service;


import com.hugmount.helloboot.product.pojo.ProductInfo;

import java.util.List;

public interface ProductService {

    int addProduct(ProductInfo productInfo);

    List<ProductInfo> getProductList(ProductInfo productInfo);

}
