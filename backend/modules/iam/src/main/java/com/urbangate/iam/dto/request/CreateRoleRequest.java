// Copyright (c) UrbanGate
package com.urbangate.iam.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(
    @NotBlank(message = "Name Cannot be Blank") String name,
    @NotBlank(message = "Description Cannot be Blank") String description) {}
