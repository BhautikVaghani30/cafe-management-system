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
import com.cafe.com.cafe.repositories.Product_Dao;
import com.cafe.com.cafe.service_Interfaces.Product_Service_interface;
import com.cafe.com.cafe.utils.CafeUtils;
import com.cafe.com.cafe.wrapper.Product_Wrapper;

@Service
public class Product_Service implements Product_Service_interface {

    @Autowired
    Product_Dao productDao;

    @Autowired
    JwtFilter jwtFilter;

    // -------------------------------------------------------------------------------------------
    // this method is implements logic of add new product
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            // only admins can add products

            if (jwtFilter.isAdmin()) {
                if (validateProductMap(requestMap, false)) {
                    Product product = productDao.findByProductName(requestMap.get("name"));
                    if (product == null) {
                        productDao.save(getProductFromMap(requestMap, false)); // add to db
                        return CafeUtils.getResponseEntity(Cafe_Constants.PRODUCT_ADDED, HttpStatus.OK);
                    }
                    return CafeUtils.getResponseEntity(Cafe_Constants.PRODUCT_ALREDY_EXIST, HttpStatus.BAD_REQUEST);
                }
                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // -------------------------------------------------------------------------------------------
    // validId is used to distinguish between the 2 use cases -- addNewProduct and

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------------------------
    // validId is used to distinguish between the 2 use cases -- addNewProduct and

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId"))); // retrieve category id as foreign key
        Product product = new Product();

        // set product attributes
        if (isAdd) {
            product.setId(Integer.parseInt(requestMap.get("id")));
        } else {
            product.setStatus("true");
        }

        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
}

    // -------------------------------------------------------------------------------------------
    // this method is implements fetch all products from db

    @Override
    public ResponseEntity<List<Product_Wrapper>> getAllProduct() {
        try {
            return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // -------------------------------------------------------------------------------------------
    // this method is implements logic of update the product
    public ResponseEntity<String> updateProduct(Map<String, String> requstMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateProductMap(requstMap, true)) {
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requstMap.get("id")));
                    if (!optional.isEmpty()) {
                        Product product = getProductFromMap(requstMap, true);
                        product.setStatus("true");
                        productDao.save(product);
                        return CafeUtils.getResponseEntity(Cafe_Constants.PRODUCT_UPDATED, HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_PRODUCT, HttpStatus.OK);
                    }
                } else {
                    return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // -------------------------------------------------------------------------------------------
    // this method is implements delete product logic
    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional = productDao.findById(id);
                if (!optional.isEmpty()) {
                    productDao.deleteById(id);
                    return CafeUtils.getResponseEntity(Cafe_Constants.PRODUCT_DELETED, HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_PRODUCT, HttpStatus.BAD_REQUEST);
                }
            }
            return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // -------------------------------------------------------------------------------------------
    // this method is implements product status logic
    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optional.isEmpty()) {
                    // invoke updateProductStatus function from the Dao
                    productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity(Cafe_Constants.PRODUCT_STATUS_UPDATED, HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_PRODUCT, HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // -------------------------------------------------------------------------------------------
    // this method is implements get product by category logic
    @Override
    public ResponseEntity<List<Product_Wrapper>> getByCategory(Integer id) {
        try {
            return new ResponseEntity<>(this.productDao.getProductByCategory(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // -------------------------------------------------------------------------------------------
    // this method is implements get product by id logic
    @Override
    public ResponseEntity<Product_Wrapper> getById(Integer id) {
        try {
            return new ResponseEntity<>(this.productDao.getProductById(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new Product_Wrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
