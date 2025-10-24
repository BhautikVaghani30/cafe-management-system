package com.cafe.com.cafe.service_Interfaces;

import org.springframework.http.ResponseEntity;

import com.cafe.com.cafe.wrapper.Product_Wrapper;

import java.util.List;
import java.util.Map;

public interface Product_Service_interface {

    // --------------------------------------------------------------------------------------------------------------
    // adds new product to a category api
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // get all product from database
    ResponseEntity<List<Product_Wrapper>> getAllProduct();

    // --------------------------------------------------------------------------------------------------------------
    // update the product in db
    ResponseEntity<String> updateProduct(Map<String, String> requstMap);

    // --------------------------------------------------------------------------------------------------------------
    // deleteProduct
    ResponseEntity<String> deleteProduct(Integer id);

    // --------------------------------------------------------------------------------------------------------------
    // Update product status
    ResponseEntity<String> updateStatus(Map<String, String> requstMap);

    // --------------------------------------------------------------------------------------------------------------
    // get product by category
    ResponseEntity<List<Product_Wrapper>> getByCategory(Integer id);

    // --------------------------------------------------------------------------------------------------------------
    // get product by id
    ResponseEntity<Product_Wrapper> getById(Integer id);

}
