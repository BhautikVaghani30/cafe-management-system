package com.cafe.com.cafe.rest_controllers;

import com.cafe.com.cafe.constants.Cafe_Constants;
import com.cafe.com.cafe.rest_Interfaces.Product_interface;
import com.cafe.com.cafe.services.Product_Service;
import com.cafe.com.cafe.utils.CafeUtils;
import com.cafe.com.cafe.wrapper.Product_Wrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class Product_Controller implements Product_interface {

  @Autowired
  Product_Service productService;

  // -------------------------------------------------------------------------------------------------------------------
  // this method is used to addNewProduct in db
  @Override
  public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
    try {
      return productService.addNewProduct(requestMap); // adds new product to a category
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // this method is used to get all product from db
  @Override
  public ResponseEntity<List<Product_Wrapper>> getAllProduct() {
    try {
      return this.productService.getAllProduct();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // this method is used to update the product in db
  @Override
  public ResponseEntity<String> updateProduct(Map<String, String> requstMap) {
    try {
      return this.productService.updateProduct(requstMap);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // this method is used to delete product
  @Override
  public ResponseEntity<String> deleteProduct(Integer id) {
    try {
      return this.productService.deleteProduct(id);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // this method is used to update product status
  @Override
  public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
    try {
      return this.productService.updateStatus(requestMap);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // this method used to fetch category by id
  @Override
  public ResponseEntity<List<Product_Wrapper>> getByCategory(Integer id) {
    try {
      return this.productService.getByCategory(id);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // -------------------------------------------------------------------------------------------------------------------
  // this method is used to get product by id
  @Override
  public ResponseEntity<Product_Wrapper> getById(Integer id) {
    try {
      return this.productService.getById(id);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(new Product_Wrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
