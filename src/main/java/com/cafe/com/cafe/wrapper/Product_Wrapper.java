package com.cafe.com.cafe.wrapper;

import lombok.Data;
@Data
public class Product_Wrapper {
    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private String status;
    private Integer categoryId;
    private String categoryName;
    public Product_Wrapper() {
    }
    public Product_Wrapper(Integer id, String name, String description, Integer price, String status,
            Integer categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
    public Product_Wrapper(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public Product_Wrapper(Integer id, String name, String description, Integer price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    


}
