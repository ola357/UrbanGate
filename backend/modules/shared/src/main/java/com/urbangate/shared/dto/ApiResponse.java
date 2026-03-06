// Copyright (c) UrbanGate
package com.urbangate.shared.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {

  private String message;
  private T data;

  public ApiResponse<T> success(T data) {
    this.data = data;
    message = "success";
    return this;
  }

  public ApiResponse<T> error(String message) {
    this.message = message;
    return this;
  }
}
