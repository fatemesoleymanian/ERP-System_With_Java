package com.example.minierp.domain.sales;

import com.example.minierp.domain.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Entity @Builder @AllArgsConstructor @NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;

    @JsonBackReference
    @ManyToOne @JoinColumn(name = "order_id")
    @NotNull(message = "سفارش الزامی است")
    private Order order;

    @ManyToOne
    @NotNull(message = "محصول الزامی است")
    private Product product;

    @Min(value = 1, message = "حداقل تعداد ۱ است")
    @Max(value = 50, message = "حداکثر تعداد ۵۰ است")
    private int quantity;

    @NotNull(message = "قیمت الزامی است")
    @DecimalMin(value = "0.0", message = "قیمت نمی‌تواند منفی باشد")
    private BigDecimal price;

    @NotNull(message = "مقدار تخفیف الزامی است")
    @DecimalMin(value = "0.0", message = "تخفیف نمی‌تواند منفی باشد")
    private BigDecimal discountValue;

    @NotNull(message = "درصد تخفیف الزامی است")
    @DecimalMin(value = "0.0", message = "تخفیف نمی‌تواند منفی باشد")
    @DecimalMax(value = "100.0", message = "تخفیف نمی‌تواند بزرگتر از صد باشد")
    private BigDecimal discountPercent;

}