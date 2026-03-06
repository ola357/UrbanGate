// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class ResourceNotFoundException extends RuntimeException {
  @Getter private String code;

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
