// Copyright (c) UrbanGate
package com.urbangate.shared.tenant;

import java.util.Optional;
import java.util.UUID;

public final class TenantContext {
  private static final ThreadLocal<String> TENANT = new ThreadLocal<>();

  private TenantContext() {}

  public static void setTenant(String tenant) {
    TENANT.set(tenant);
  }

  public static Optional<String> getTenant() {
    return Optional.ofNullable(TENANT.get());
  }

  public static UUID requireTenantId() {
    String raw = TENANT.get();
    if (raw == null || raw.isBlank()) {
      throw new IllegalStateException("Tenant id is not set");
    }
    return UUID.fromString(raw);
  }

  public static void clear() {
    TENANT.remove();
  }
}
