package com.cafe.com.cafe.services;

import com.cafe.com.cafe.repositories.Bill_Dao;
import com.cafe.com.cafe.repositories.Category_Dao;
import com.cafe.com.cafe.repositories.Order_Dao;
import com.cafe.com.cafe.repositories.Product_Dao;
import com.cafe.com.cafe.repositories.User_Dao;
import com.cafe.com.cafe.service_Interfaces.Dashboard_Service_interface;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class Dashboard_Service implements Dashboard_Service_interface {

    @Autowired
    Category_Dao categoryDao;

    @Autowired
    Product_Dao productDao;

    @Autowired
    Bill_Dao billDao;

    @Autowired
    Order_Dao order_Dao;

    @Autowired
    User_Dao user_Dao;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        try {
           
            Map<String, Object> map = new HashMap<>();
            // map each table to a count
            map.put("category", categoryDao.count());
            map.put("product", productDao.count());
            map.put("bill", billDao.count());
            map.put("Orders", order_Dao.count());
            map.put("Staff", user_Dao.count());
            map.put("totalPayment", order_Dao.sumTotal());
            map.put("cards", order_Dao.sumTotalForPaymentMethod("Credit Card") + order_Dao.sumTotalForPaymentMethod("Debit Card"));
            map.put("cash", order_Dao.sumTotalForPaymentMethod("cash"));
            map.put("online", order_Dao.sumTotalForPaymentMethod("Online Payment"));
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
