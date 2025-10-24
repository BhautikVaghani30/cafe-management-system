package com.cafe.com.cafe.repositories;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cafe.com.cafe.Entites.Order;

public interface Order_Dao extends JpaRepository<Order,Integer> {

    @Query("SELECT o FROM Order o ORDER BY o.id DESC")
    List<Order> findAllOrders();
    
    @Query("SELECT o FROM Order o WHERE o.date = CURRENT_DATE ORDER BY o.id DESC")
    List<Order> findAllOrdersByDate();

    @Query("SELECT o FROM Order o WHERE o.category = :category AND o.date = CURRENT_DATE ORDER BY o.id DESC")
    List<Order> findByCategory(String category);

    @Query("SELECT SUM(o.total) FROM Order o")
    Double sumTotal();

    @Query("SELECT COALESCE(SUM(o.total), 0.0) FROM Order o WHERE o.paymentMethod = :paymentMethod")
    Double sumTotalForPaymentMethod(String paymentMethod);

    @Query("select b from Order b where b.createdBy=:username Order by b.id desc")
    List<Order> getOrdeByUserName(@Param("username") String username);
}