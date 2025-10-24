package com.cafe.com.cafe.Entites;

import java.io.Serializable;
import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "orders")
public class Order implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "category")
    private String category;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "price")
    private String price;

    @Column(name = "total")
    private int total;

    @Column(name = "order_status")
    private boolean orderStatus;
    
    @Column(name = "billuuid")
    private String bill_uuid;

    @Column(name = "tablenumber")
    private String tablenumber;

    @Column(name = "paymentMethod")
    private String paymentMethod;

    @Column(name = "date")
    public LocalDate date = LocalDate.now();

    @Column(name = "createdBy")
    public String createdBy;
}
// Name: Product1
// Category: Category1
// Quantity: 1
// Price: 10.0
// Total: 10.0