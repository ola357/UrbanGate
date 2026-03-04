// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class DataBaseOperationException extends RuntimeException {
  @Getter private final String code;

  public DataBaseOperationException(String message) {
    super(message);
    this.code = null;
  }

  public DataBaseOperationException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
