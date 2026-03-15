// Copyright (c) UrbanGate
package com.urbangate.iam.dto.response;

import java.util.Set;
import java.util.UUID;

public record MyTenantItem(
    UUID tenantId, String tenantSlug, String tenantName, Set<String> roles) {}
