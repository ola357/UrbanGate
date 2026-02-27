// Copyright (c) UrbanGate
package com.urbangate.shared.enums;

import lombok.Getter;

public enum ExceptionResponse {
  USER_NAME_NOTFOUND("1000", "The Username is not found"),
  UNABLE_TO_INSERT_RECORD("1001", "Database Insertion failed"),
  UNABLE_TO_FETCH_RECORD("1002", "Unable to fetch record"),
  UNABLE_TO_REVOKE_ACTIVATION_CODE("1003", "Could not revoke activation code"),
  INVALID_ACTIVATION_CODE("1004", "Invalid activation code"),
  ACTIVATION_CODE_REVOKED("1005", "Activation code revoked"),
  PHONE_NUMBER_ALREADY_REGISTERED("1006", "Phone number already registered"),
  EMAIL_ALREADY_REGISTERED("1007", "Email already registered"),
  TENANT_ALREADY_REGISTERED("1008", "Tenant already registered"),
  ROLE_NOT_FOUND("1009", "Role Not Found"),
  INVALID_RESET_CODE("1010", "Invalid or expired reset code");

  @Getter private String code;
  @Getter private String message;

  ExceptionResponse(String code, String message) {
    this.message = message;
    this.code = code;
  }
}
