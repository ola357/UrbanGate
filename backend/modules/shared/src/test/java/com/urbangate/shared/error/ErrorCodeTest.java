// Copyright (c) UrbanGate
package com.urbangate.shared.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ErrorCodeTest {

  @Test
  void exposesExpectedValues() {
    assertEquals(6, ErrorCode.values().length);
    assertEquals("VALIDATION_ERROR", ErrorCode.VALIDATION_ERROR.name());
    assertEquals("INTERNAL_ERROR", ErrorCode.INTERNAL_ERROR.name());
  }
}
