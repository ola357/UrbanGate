// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class InvalidResetTokenException extends RuntimeException {
  @Getter private final String code;

  public InvalidResetTokenException(String message) {
    super(message);
    this.code = null;
  }

  public InvalidResetTokenException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
