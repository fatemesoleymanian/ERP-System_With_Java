package com.example.minierp.domain.sales;

import com.example.minierp.domain.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Entity @Builder @AllArgsConstructor @NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue
    private long id;

    @JsonBackReference
    @ManyToOne @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    private Product product;

    private int quantity;

    private double price;
}
