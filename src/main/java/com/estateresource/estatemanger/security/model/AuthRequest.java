package com.estateresource.estatemanger.security.model;

import jakarta.validation.constraints.NotBlank;


public record AuthRequest(
        @NotBlank(message = "Phone Number Cannot be Blank")
        String phoneNumber,

        @NotBlank(message = "Password Cannot be Blank")
        String password
) {

}
