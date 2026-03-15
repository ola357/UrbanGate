// Copyright (c) UrbanGate
package com.urbangate.iam.dto.request;

import jakarta.validation.constraints.Email;

public record PasswordRecoveryRequest(@Email String email) {}
