package com.example.minierp.interfaces.rest.customer;

import com.example.minierp.domain.customer.CustomerType;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateCustomerRequest(
        @NotBlank(message = "نام مشتری الزامی است")
        @Size(max = 100, message = "نام مشتری نمی‌تواند بیشتر از ۱۰۰ کاراکتر باشد")
        String name,

        @NotNull(message = "نوع مشتری الزامی است")
        CustomerType type,

        @Size(max = 100, message = "نام شخص تماس نمی‌تواند بیشتر از ۱۰۰ کاراکتر باشد")
        String contactPerson,

        @Pattern(regexp = "^[0-9\\-+]{7,15}$", message = "فرمت شماره تلفن نامعتبر است")
        String phone,

        @Email(message = "آدرس ایمیل نامعتبر است")
        String email,

        @Size(max = 255, message = "آدرس صورتحساب نمی‌تواند بیشتر از ۲۵۵ کاراکتر باشد")
        String billingAddress,

        @Size(max = 255, message = "آدرس ارسال کالا نمی‌تواند بیشتر از ۲۵۵ کاراکتر باشد")
        String shippingAddress,

        @DecimalMin(value = "0.0", message = "سقف اعتبار نمی‌تواند منفی باشد")
        Double creditLimit
) {}
