// Copyright (c) UrbanGate
package com.urbangate.iam.dto.response;

import java.time.Instant;
import java.util.UUID;

public record InviteUserResponse(UUID invitationId, Instant expiresAt, String token) {}
