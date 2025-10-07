package com.example.minierp.domain.sales;

import com.example.minierp.domain.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private SaleOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private BigDecimal price;
    private BigDecimal discountValue;
    private BigDecimal discountPercent;

    public BigDecimal getTotal() {
        BigDecimal discount = (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0)
                ? price.multiply(discountPercent).divide(BigDecimal.valueOf(100))
                : discountValue != null ? discountValue : BigDecimal.ZERO;

        return price.subtract(discount).multiply(BigDecimal.valueOf(quantity));
    }
}
