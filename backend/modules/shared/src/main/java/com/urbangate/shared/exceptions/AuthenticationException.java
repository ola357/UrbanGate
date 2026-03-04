// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class AuthenticationException extends RuntimeException {

  @Getter private final String code;

  public AuthenticationException(String message, String code) {
    super(message);
    this.code = code;
  }

  public AuthenticationException(String message) {
    super(message);
    this.code = null;
  }

  public AuthenticationException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
