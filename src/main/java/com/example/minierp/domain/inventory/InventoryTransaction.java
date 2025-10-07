package com.example.minierp.domain.inventory;

import com.example.minierp.domain.common.AuditableEntity;
import com.example.minierp.domain.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransaction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private InventoryTransactionType type; // IN or OUT

    private Long orderId; // optional, links to order if OUT
}
