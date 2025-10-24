package com.cafe.com.cafe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafe.com.cafe.Entites.Category;
import com.cafe.com.cafe.Entites.Product;
import com.cafe.com.cafe.JWT.JwtFilter;
import com.cafe.com.cafe.constants.Cafe_Constants;
import com.cafe.com.cafe.repositories.Category_Dao;
import com.cafe.com.cafe.repositories.Product_Dao;
import com.cafe.com.cafe.service_Interfaces.Category_Service_interface;
import com.cafe.com.cafe.utils.CafeUtils;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class Category_Service implements Category_Service_interface {

    @Autowired
    Category_Dao categoryDao;

    @Autowired
    Product_Dao product_Dao;

    @Autowired
    JwtFilter jwtFilter;

    // ---------------------------------------------------------------------------------------------------------------
    // this is for add new category
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            // only admins can add categories
            if (jwtFilter.isAdmin()) {

                // this method created below
                if (validateCategoryMap(requestMap, false)) {
                    Category category = categoryDao.findByName(requestMap.get("name"));
                    if(category == null){
                        // this method created below
                        categoryDao.save(getCategoryFromMap(requestMap, false)); // add to db
                        return CafeUtils.getResponseEntity(Cafe_Constants.CATEGORY_ADDED, HttpStatus.OK);
                    }
                    return CafeUtils.getResponseEntity(Cafe_Constants.CATEGORY_ALREDY_EXIST, HttpStatus.BAD_REQUEST);
                    
                }

                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);

            }

            else {
                return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ---------------------------------------------------------------------------------------------------------------
    // validId is used to distinguish between the 2 use cases -- addNewCategory and
    // updateCategory
    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------------------------------------------------------
    // validId is used to distinguish between the 2 use cases -- addNewCategory and
    // updateCategory
    private Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();

        if (isAdd) {
            category.setId(Integer.parseInt(requestMap.get("id")));
        }

        category.setName(requestMap.get("name"));
        return category;
    }

    // ---------------------------------------------------------------------------------------------------------------
    // get all category
    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
                log.info("insaid if");
                return new ResponseEntity<List<Category>>(this.categoryDao.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(this.categoryDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ---------------------------------------------------------------------------------------------------------------
    // update category
    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateCategoryMap(requestMap, true)) {
                    // find the specified category
                    Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()) {
                        categoryDao.save(getCategoryFromMap(requestMap, true)); // update to db
                        return CafeUtils.getResponseEntity(Cafe_Constants.CATEGORY_UPDATED, HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_CATEGORY, HttpStatus.OK);
                    }
                }
                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> deleteCategory(Integer id) {
        try {
            // Check if category exists
            Optional<Category> optionalCategory = categoryDao.findById(id);
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();

                // Check if there are associated products
                List<Product> products = product_Dao.findByCategory(category);
                if (products.isEmpty()) {
                    // No associated products, delete the category
                    categoryDao.deleteById(id);
                    return CafeUtils.getResponseEntity(Cafe_Constants.CATEGORY_DELETE, HttpStatus.OK);
                } else {
                    // Associated products exist, delete them first
                    product_Dao.deleteAll(products);

                    // Now delete the category
                    categoryDao.deleteById(id);
                    return CafeUtils.getResponseEntity(Cafe_Constants.CATEGORY_DELETE, HttpStatus.OK);
                }
            } else {
                // Category not found
                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_CATEGORY, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void test() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
