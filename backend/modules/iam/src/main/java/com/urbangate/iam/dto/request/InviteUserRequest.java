// Copyright (c) UrbanGate
package com.urbangate.iam.dto.request;

import com.urbangate.iam.domain.enums.TenantRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record InviteUserRequest(@Email String email, @NotNull TenantRole role) {}
