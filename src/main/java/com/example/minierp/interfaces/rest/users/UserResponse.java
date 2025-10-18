package com.example.minierp.interfaces.rest.users;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "پاسخ کاربران")
public record UserResponse(
        @Schema(description = "شناسه کاربر")
        Long id,

        @Schema(description = "نام کاربری کاربر")
        String userName,

        @Schema(description = "نقش کاربر")
        String role,

        @Schema(description = "فعال بودن کاربر")
        Boolean active

) {
}
