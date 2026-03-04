// Copyright (c) UrbanGate
package com.urbangate.iam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
    @NotBlank(message = "Code cannot be Blank") String code,
    @NotBlank(message = "Password Cannot be Blank")
        @Pattern(
            regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message =
                "Password must be at least 8 characters and include uppercase, lowercase, digit, and special character")
        String password,
    @NotBlank(message = "Email cannot be Blank") String email) {}
