package com.cafe.com.cafe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cafe.com.cafe.Entites.Category;
import com.cafe.com.cafe.Entites.Product;
import com.cafe.com.cafe.wrapper.Product_Wrapper;

import jakarta.transaction.Transactional;

public interface Product_Dao extends JpaRepository<Product, Integer> {
   
   List<Product_Wrapper> getAllProduct();
   
   @Modifying
   @Transactional
   Integer updateProductStatus(@Param("status") String status,@Param("id") Integer id);

   List<Product_Wrapper> getProductByCategory(@Param(value = "id") Integer id);

   Product_Wrapper getProductById(@Param(value = "id") Integer id);

   List<Product> findByCategory(Category category);

   @Query("SELECT p FROM Product p WHERE p.name = :name")
   Product findByProductName(@Param("name") String name);
}
