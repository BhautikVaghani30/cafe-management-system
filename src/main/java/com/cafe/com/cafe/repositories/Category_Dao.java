package com.cafe.com.cafe.repositories;

import com.cafe.com.cafe.Entites.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// this is category repository 

public interface Category_Dao extends JpaRepository<Category, Integer> {
    List<Category> getAllCategory();
    @Query("SELECT c FROM Category c WHERE c.name = :name")
    Category findByName(@Param("name") String name);
}
