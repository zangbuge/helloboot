package com.hugmount.helloboot.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hugmount.helloboot.product.mapper.ProductMapper;
import com.hugmount.helloboot.product.pojo.ProductInfo;
import com.hugmount.helloboot.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Override
    public int addProduct(ProductInfo info) {
        int qt = productMapper.insertProduct(info);
        return qt;
    }

    @Override
    public List<ProductInfo> getProductList(ProductInfo productInfo) {
        PageHelper.startPage(2, 2);
        List<ProductInfo> list =  productMapper.getProductList(productInfo);
        PageInfo<ProductInfo> pageInfo = new PageInfo<>(list);
        System.out.println(JSON.toJSONString(pageInfo));
        return list;
    }

}
