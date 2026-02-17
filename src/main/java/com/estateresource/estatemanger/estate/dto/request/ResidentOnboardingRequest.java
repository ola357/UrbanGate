package com.estateresource.estatemanger.estate.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResidentOnboardingRequest(
        @NotBlank(message = "First Name Cannot be Blank")
        String firstName,
        @NotBlank(message = "Last Name Cannot be Blank")
        String lastName,
        String email,
        @NotBlank(message = "Phone Number Cannot be Blank")
        String phoneNumber,
        @NotBlank(message = "Unit Address Cannot be Blank")
        String unitAddress,
        String gender
) {
}
