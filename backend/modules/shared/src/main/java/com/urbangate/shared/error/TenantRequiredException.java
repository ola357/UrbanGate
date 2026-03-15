// Copyright (c) UrbanGate
package com.urbangate.shared.error;

public class TenantRequiredException extends RuntimeException {
  public TenantRequiredException() {
    super("Tenant header is required");
  }
}
