// Copyright (c) UrbanGate
package com.urbangate.shared.error;

public class TenantForbiddenException extends RuntimeException {
  public TenantForbiddenException(String tenantId) {
    super("Authenticated user is not allowed to access tenant " + tenantId);
  }
}
