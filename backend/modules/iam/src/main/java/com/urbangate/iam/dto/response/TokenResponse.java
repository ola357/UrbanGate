// Copyright (c) UrbanGate
package com.urbangate.iam.dto.response;

public record TokenResponse(
    String accessToken, String refreshToken, String expiresIn, String scope) {}
