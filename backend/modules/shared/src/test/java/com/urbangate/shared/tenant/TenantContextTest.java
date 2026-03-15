// Copyright (c) UrbanGate
package com.urbangate.shared.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TenantContextTest {

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  void setTenantStoresValue() {
    UUID tenantId = UUID.randomUUID();
    TenantContext.setTenant(tenantId.toString());

    assertTrue(TenantContext.getTenant().isPresent());
    assertEquals(tenantId.toString(), TenantContext.getTenant().orElseThrow());
    assertEquals(tenantId, TenantContext.requireTenantId());
  }

  @Test
  void requireTenantIdThrowsWhenMissing() {
    assertFalse(TenantContext.getTenant().isPresent());
    assertThrows(IllegalStateException.class, TenantContext::requireTenantId);
  }
}
