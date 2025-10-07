package com.example.minierp.domain.sales;

import com.example.minierp.domain.common.AuditableEntity;
import com.example.minierp.domain.customer.Customer;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sale_orders")
public class SaleOrder extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal orderDiscountValue;
    private BigDecimal orderDiscountPercent;

    private String cancelReason;

    private LocalDateTime desiredDeliveryFrom;
    private LocalDateTime desiredDeliveryTo;

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}
