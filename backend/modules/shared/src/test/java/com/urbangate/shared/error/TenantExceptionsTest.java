// Copyright (c) UrbanGate
package com.urbangate.shared.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TenantExceptionsTest {

  @Test
  void tenantRequiredExceptionHasMessage() {
    assertEquals("Tenant header is required", new TenantRequiredException().getMessage());
  }

  @Test
  void tenantForbiddenExceptionHasMessage() {
    assertEquals(
        "Authenticated user is not allowed to access tenant 123",
        new TenantForbiddenException("123").getMessage());
  }
}
