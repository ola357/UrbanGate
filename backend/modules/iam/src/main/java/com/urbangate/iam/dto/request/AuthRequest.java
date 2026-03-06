// Copyright (c) UrbanGate
package com.urbangate.iam.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
    @NotBlank(message = "Phone Number Cannot be Blank") String phoneNumber,
    @NotBlank(message = "Password Cannot be Blank") String password,
    String realm) {}
