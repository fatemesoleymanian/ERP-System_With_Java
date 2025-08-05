package com.example.minierp.interfaces.rest.sales;

import java.util.List;

public record CreateOrderRequest(List<Item> items) {
    public record Item(long productId, int quantity) {}
}
