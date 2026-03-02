// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class ConflictException extends RuntimeException {

  @Getter private final String code;

  public ConflictException(String message) {
    super(message);
    this.code = null;
  }

  public ConflictException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
