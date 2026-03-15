// Copyright (c) UrbanGate
package com.urbangate.shared.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TenantHeaderConstantsTest {

  @Test
  void exposesExpectedHeaderName() {
    assertEquals("X-Tenant-Id", TenantHeaderConstants.TENANT_ID);
  }
}
