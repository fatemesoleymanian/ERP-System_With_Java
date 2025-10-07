package com.example.minierp.domain.user;

public enum Role {
    ADMIN, //دسترسی کامل به همه ماژول‌ها (Sales, Inventory, Customer)
    SALES, //فقط مدیریت سفارش‌ها و مشتری‌ها
    INVENTORY_MANAGER; //فقط مدیریت محصولات و موجودی

}
