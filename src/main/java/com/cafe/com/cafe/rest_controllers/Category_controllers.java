package com.cafe.com.cafe.rest_controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.cafe.com.cafe.Entites.Category;
import com.cafe.com.cafe.constants.Cafe_Constants;
import com.cafe.com.cafe.rest_Interfaces.Category_interface;
import com.cafe.com.cafe.service_Interfaces.Category_Service_interface;
import com.cafe.com.cafe.utils.CafeUtils;

@RestController
public class Category_controllers implements Category_interface {

    // --------------------------------------------------------------------------------------------------------------
    @Autowired
    Category_Service_interface categoryService;

    // --------------------------------------------------------------------------------------------------------------
    // add new category
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            return categoryService.addNewCategory(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // --------------------------------------------------------------------------------------------------------------
    // get all category
    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            return this.categoryService.getAllCategory(filterValue);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --------------------------------------------------------------------------------------------------------------
    // update category
    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
       try {
        return this.categoryService.updateCategory(requestMap);
       } catch (Exception e) {
        e.printStackTrace();
       }
       return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> deleteCategory(Integer id) {
        try {
            return categoryService.deleteCategory(id);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
