// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class ResourceNotFoundException extends RuntimeException {
  @Getter private final String code;

  public ResourceNotFoundException(String message) {
    super(message);
    this.code = null;
  }

  public ResourceNotFoundException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
