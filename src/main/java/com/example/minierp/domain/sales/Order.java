package com.example.minierp.domain.sales;


import com.example.minierp.domain.customer.Customer;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "شماره سفارش الزامی است")
    private String orderNumber;

    @CreatedDate
    @Column(updatable = false)
    @NotNull(message = "تاریخ ایجاد الزامی است")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @NotNull(message = "تاریخ آخرین تغییر الزامی است")
    private LocalDateTime lastModifiedDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "وضعیت سفارش الزامی است")
    private OrderStatus status;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotEmpty(message = "سفارش باید حداقل یک آیتم داشته باشد")
    @Size(max = 20, message = "سفارش نمی‌تواند بیش از ۲۰ آیتم داشته باشد")
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull(message = "مشتری الزامی است")
    private Customer customer;

    /** مبلغ کل بدون مالیات */
    @NotNull(message = "مبلغ کل بدون مالیات الزامی است")
    @DecimalMin(value = "0.0", message = "مبلغ کل بدون مالیات نمی‌تواند منفی باشد")
    private BigDecimal subTotal;

    /** مالیات */
    @NotNull(message = "مبلغ مالیات الزامی است")
    @DecimalMin(value = "0.0", message = "مبلغ مالیات نمی‌تواند منفی باشد")
    private BigDecimal taxAmount;

    /** مبلغ نهایی */
    @NotNull(message = "مبلغ نهایی الزامی است")
    @DecimalMin(value = "0.0", message = "مبلغ نهایی نمی‌تواند منفی باشد")
    private BigDecimal totalAmount;

    @NotNull(message = "مقدار تخفیف سفارش الزامی است")
    @DecimalMin(value = "0.0", message = "تخفیف سفارش نمی‌تواند منفی باشد")
    private BigDecimal orderDiscountValue;

    @NotNull(message = "درصد تخفیف سفارش الزامی است")
    @DecimalMin(value = "0.0", message = "درصد تخفیف سفارش نمی‌تواند منفی باشد")
    @DecimalMax(value = "100.0", message = "درصد تخفیف سفارش نمی‌تواند بزرگتر از صد باشد")
    private BigDecimal orderDiscountPercent;

    /** دلیل لغو */
    private String cancelReason;

    /** کاربر سازنده */
    @CreatedBy
    @Column(updatable = false)
    @NotBlank(message = "کاربر سازنده الزامی است")
    private String createdBy;

    /** آخرین کاربر ویرایش‌کننده */
    @LastModifiedBy
    @NotBlank(message = "آخرین کاربر ویرایش‌کننده الزامی است")
    private String lastModifiedBy;
    /** بازه زمانی مورد نظر مشتری برای ارسال - شروع */
    private LocalDateTime desiredDeliveryFrom;

    /** بازه زمانی مورد نظر مشتری برای ارسال - پایان */
    private LocalDateTime desiredDeliveryTo;
    @Version
    private Long version;
}