// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class DataBaseOperationException extends RuntimeException {
  @Getter private String code;

  public DataBaseOperationException(String message) {
    super(message);
  }

  public DataBaseOperationException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
