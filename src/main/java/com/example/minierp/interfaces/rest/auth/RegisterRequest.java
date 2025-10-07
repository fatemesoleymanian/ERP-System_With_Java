package com.example.minierp.interfaces.rest.auth;

import com.example.minierp.domain.user.Role;
import com.example.minierp.infrastructure.validation.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "نام کاربری الزامی است")
        @Size(max = 50, message = "نام کاربری نمی‌تواند بیشتر از 50 کاراکتر باشد")
        String username,
        @NotBlank(message = "رمزعبور الزامی است")
        @Size(max = 20, message = "رمزعبور نمی‌تواند بیشتر از 20 کاراکتر باشد")
        @Size(min = 5, message = "رمزعبور نمی‌تواند کمتر از 5 کاراکتر باشد")
        String password,

        @NotNull(message = "نقش الزامی است")
        @ValidEnum(enumClass = Role.class, message = "نقش واردشده معتبر نیست")
        String role
) { }
