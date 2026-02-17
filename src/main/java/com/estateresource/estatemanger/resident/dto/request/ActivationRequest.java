package com.estateresource.estatemanger.resident.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ActivationRequest(
        @NotBlank(message = "Token Cannot be Blank")
        String token
) {
}
