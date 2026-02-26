// Copyright (c) UrbanGate
package com.urbangate.iam.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResidentOnboardingRequest(
    @NotBlank(message = "First Name Cannot be Blank") String firstName,
    @NotBlank(message = "Last Name Cannot be Blank") String lastName,
    String email,
    @NotBlank(message = "Phone Number Cannot be Blank") String phoneNumber,
    @NotBlank(message = "Unit Address Cannot be Blank") String unitAddress,
    @NotBlank(message = "Role Cannot be Blank") String role,
    String gender) {}
