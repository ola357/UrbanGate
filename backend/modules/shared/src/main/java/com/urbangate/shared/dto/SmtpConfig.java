// Copyright (c) UrbanGate
package com.urbangate.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record SmtpConfig(
    @NotBlank(message = "Host Cannot be Blank") String host,
    @NotBlank(message = "Port Cannot be Blank") String port,
    @NotBlank(message = "From Cannot be Blank") String from,
    @NotBlank(message = "User Cannot be Blank") String user,
    @NotBlank(message = "Password Cannot be Blank") String password) {}
