package com.cafe.com.cafe.wrapper;

import lombok.Data;


@Data

public class TransactionDetails {
    private String orderId;
    private String currency;
    private Integer amount;
    public TransactionDetails(String orderId, String currency, Integer amount) {
        this.orderId = orderId;
        this.currency = currency;
        this.amount = amount;
    }
    public TransactionDetails() {}

}
