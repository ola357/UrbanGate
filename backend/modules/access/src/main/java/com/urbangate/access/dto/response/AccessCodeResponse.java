// Copyright (c) UrbanGate
package com.urbangate.access.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.urbangate.access.enums.AccessType;
import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccessCodeResponse(
    String accessCode,
    Timestamp expirationDate,
    AccessType accessType,
    Timestamp startFromTime,
    boolean active,
    String visitorName) {}
