// Copyright (c) UrbanGate
package com.urbangate.iam.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AcceptInviteRequest(@NotBlank String token) {}
