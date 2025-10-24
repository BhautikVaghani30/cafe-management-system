package com.cafe.com.cafe.repositories;

import com.cafe.com.cafe.Entites.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Bill_Dao extends JpaRepository<Bill, Integer> {

    List<Bill> getAllBills();

    List<Bill> getBillByUserName(@Param("username") String username);

    // Add a method to find a bill by UUID
    Bill findByUuid(String uuid);
    
}
