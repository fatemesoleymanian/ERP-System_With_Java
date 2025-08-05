package com.example.minierp.domain.inventory;

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
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Product product;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private InventoryTransactionType type; // IN / OUT

    private LocalDateTime timestamp;
}
