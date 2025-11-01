package com.example.minierp.domain.sales;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    DRAFT("پیش‌نویس"),// Order created but not yet confirmed
    PLACED("ثبت شده"),// Customer placed the order
    CONFIRMED("تایید شده"),// Admin confirmed and stock reserved
    PAID("پرداخت شده"),// Payment completed
    SHIPPED("ارسال شده"),// Order sent to customer
    COMPLETED("تکمیل شده"),// Order received and finalized
    CANCELLED("لغو شده");// Order cancelled by customer/admin

    // 1. Add a final field for the translation
    private final String farsiName;

    // The @Getter and @RequiredArgsConstructor annotations from Lombok
    // automatically create the getter method (getFarsiName()) and the constructor.
    // If you are not using Lombok, you would write them manually like this:
    /*
        private final String farsiName;

        OrderStatus(String farsiName) {
            this.farsiName = farsiName;
        }

        public String getFarsiName() {
            return farsiName;
        }
    */
}