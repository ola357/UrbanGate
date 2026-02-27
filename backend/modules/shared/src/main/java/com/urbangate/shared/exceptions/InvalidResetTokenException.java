// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class InvalidResetTokenException extends RuntimeException {
  @Getter private String code;

  public InvalidResetTokenException(String message) {
    super(message);
  }

  public InvalidResetTokenException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
