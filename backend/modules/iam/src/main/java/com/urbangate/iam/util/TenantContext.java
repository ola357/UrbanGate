// Copyright (c) UrbanGate
package com.urbangate.iam.util;

public final class TenantContext {

  private TenantContext() {}

  private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

  public static void setTenantId(String tenantId) {
    CURRENT_TENANT.set(tenantId);
  }

  public static String getTenantId() {
    String tenant = CURRENT_TENANT.get();
    if (tenant == null) {
      throw new IllegalStateException("No tenant in context");
    }
    return tenant;
  }

  public static void clear() {
    CURRENT_TENANT.remove();
  }
}
