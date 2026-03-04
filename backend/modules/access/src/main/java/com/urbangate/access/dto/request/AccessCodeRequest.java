// Copyright (c) UrbanGate
package com.urbangate.access.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.urbangate.access.enums.AccessType;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccessCodeRequest(
    String visitorName,
    String visitorPhone,
    String visitorEmail,
    String visitorType,
    Timestamp startDate,
    Timestamp expiryDate,
    String purposeOfVisit,
    int noOfGuests,
    String groupName,
    AccessType accessType) {}
