package com.example.minierp.interfaces.rest.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "پاسخ احراز هویت")

public record AuthResponse (
        @Schema(description = "توکن کاربر")
        String token,
        @Schema(description = "نقش کاربر")
        String role
){ }
