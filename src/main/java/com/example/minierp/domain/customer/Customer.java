package com.example.minierp.domain.customer;

import com.example.minierp.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customers")
public class Customer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // شماره مشتری برای رفرنس انسانی (مثل CUST-2025-0001)
    @Column(nullable = false, unique = true, updatable = false)
    private String customerNumber;

    @Column(nullable = false)
    private String name;

    private String contactPerson;
    private String phone;
    private String email;

    private String billingAddress;
    private String shippingAddress;

    // نوع مشتری: فردی یا شرکتی
    @Enumerated(EnumType.STRING)
    private CustomerType type;

    // اطلاعات مالی پایه
    private Double creditLimit;   // سقف اعتبار
    @Column(nullable = false,  columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean active;       // فعال/غیرفعال

}
