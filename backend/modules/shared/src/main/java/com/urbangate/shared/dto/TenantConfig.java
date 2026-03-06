// Copyright (c) UrbanGate
package com.urbangate.shared.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TenantConfig(
    @NotBlank(message = "Display Name Cannot be Blank") String displayName,
    @NotNull(message = "Stmp Config Cannot be Null") SmtpConfig smtpConfig,
    @NotNull(message = "Additional Information Cannot be null") @Valid
        TenantAdditionalInfo tenantAdditionalInfo) {}
