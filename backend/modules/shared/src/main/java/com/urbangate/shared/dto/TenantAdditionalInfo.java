// Copyright (c) UrbanGate
package com.urbangate.shared.dto;

import com.urbangate.shared.enums.PayableBills;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TenantAdditionalInfo(
    @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,
    @Size(max = 255, message = "Description must not exceed 255 characters") String description,
    @Size(max = 255, message = "Icon must not exceed 255 characters") String icon,
    String creator,
    @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address,
    @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must not exceed 100 characters")
        String state,
    @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9+]{7,15}$", message = "Phone must be a valid number")
        String phone,
    @Min(value = 0, message = "Days before overdue must be >= 0") int numberOfDaysBeforeOverdue,
    @Min(value = 0, message = "Days before upcoming payment must be >= 0")
        int numberOfDaysBeforeUpcomingPayment,
    String estateCode,
    boolean sendBirthdayShout,
    @Min(value = 0, message = "Maximum guests must be >= 0") int maximumGuestsForMultipleCode,
    @NotEmpty(message = "Payable bills cannot be empty") @Valid List<PayableBills> payableBills) {}
