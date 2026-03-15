// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetCode(UUID resetId, String code, Instant expiresAt) {}
