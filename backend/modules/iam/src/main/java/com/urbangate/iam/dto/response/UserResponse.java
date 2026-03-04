// Copyright (c) UrbanGate
package com.urbangate.iam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile response")
public record UserResponse(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    Map<String, List<String>> attributes) {}
