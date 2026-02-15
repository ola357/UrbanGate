package com.estateresource.estatemanger.estate.dto.request;

import com.estateresource.estatemanger.security.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResidentOnboardingRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String unitAddress,
        String gender
) {
}
