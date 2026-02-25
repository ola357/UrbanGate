// Copyright (c) UrbanGate
package com.urbangate.shared.exceptions;

import com.urbangate.shared.enums.ExceptionResponse;
import lombok.Getter;

public class UserNameNotFoundException extends RuntimeException {
  @Getter private String code;

  public UserNameNotFoundException(String message) {
    super(message);
  }

  public UserNameNotFoundException(ExceptionResponse response) {
    super(response.getMessage());
    this.code = response.getCode();
  }
}
