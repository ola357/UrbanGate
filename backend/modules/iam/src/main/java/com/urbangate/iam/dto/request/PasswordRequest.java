// Copyright (c) UrbanGate
package com.urbangate.iam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordRequest(
    @NotBlank(message = "Password Cannot be Blank")
        @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message =
                "Password must be at least 8 characters and include uppercase, lowercase, digit, and special character")
        String password,
    @NotBlank(message = "UserId Cannot be Blank") String userId,
    @NotBlank(message = "Activation Code Cannot be Blank") String activationToken) {}
