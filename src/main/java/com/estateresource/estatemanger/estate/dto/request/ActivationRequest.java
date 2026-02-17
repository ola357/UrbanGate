package com.estateresource.estatemanger.estate.dto.request;


import jakarta.validation.constraints.NotBlank;

public record ActivationRequest(
        @NotBlank(message = "Token Should not be blank")
        String token
) {
}
