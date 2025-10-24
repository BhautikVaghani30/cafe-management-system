package com.cafe.com.cafe.service_Interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.cafe.com.cafe.Entites.Category;

public interface Category_Service_interface {

    // --------------------------------------------------------------------------------------------------------------
    // adds new category to the category api
    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // get all category
    ResponseEntity<List<Category>> getAllCategory(String filterValue);

    // --------------------------------------------------------------------------------------------------------------
    // update category
    ResponseEntity<String> updateCategory(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // delete category
    ResponseEntity<String> deleteCategory(Integer id);
}
