// Copyright (c) UrbanGate
package com.urbangate.app.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.urbangate.shared.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;

class ApiExceptionHandlerTest {

  @Test
  void handleGenericReturnsProblemDetail() {
    var handler = new ApiExceptionHandler();
    var request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1/test");

    ProblemDetail detail = handler.handleGeneric(new RuntimeException("boom"), request);

    assertThat(detail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(detail.getTitle()).isEqualTo("Internal error");
    assertThat(detail.getDetail()).isEqualTo("Unexpected error.");
    assertThat(detail.getProperties()).containsEntry("code", ErrorCode.INTERNAL_ERROR.name());
    assertThat(detail.getProperties()).containsEntry("path", "/api/v1/test");
    assertThat(detail.getProperties()).containsKey("timestamp");
  }
}
