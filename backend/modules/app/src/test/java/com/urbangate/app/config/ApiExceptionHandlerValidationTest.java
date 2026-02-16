// Copyright (c) UrbanGate
package com.urbangate.app.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.urbangate.shared.error.ErrorCode;
import jakarta.validation.Valid;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ApiExceptionHandlerValidationTest {

  @Test
  void handleValidationReturnsProblemDetail() throws Exception {
    var handler = new ApiExceptionHandler();
    var request = new MockHttpServletRequest();
    request.setRequestURI("/test/validate");

    Method method = DummyController.class.getDeclaredMethod("create", Payload.class);
    MethodParameter parameter = new MethodParameter(method, 0);
    var bindingResult = new BeanPropertyBindingResult(new Payload(null), "payload");
    bindingResult.addError(new FieldError("payload", "name", "must not be blank"));
    var ex = new MethodArgumentNotValidException(parameter, bindingResult);

    ProblemDetail detail = handler.handleValidation(ex, request);

    assertThat(detail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(detail.getTitle()).isEqualTo("Validation error");
    assertThat(detail.getDetail()).isEqualTo("One or more fields are invalid.");
    assertThat(detail.getProperties()).containsEntry("code", ErrorCode.VALIDATION_ERROR.name());
    assertThat(detail.getProperties()).containsEntry("path", "/test/validate");
    assertThat(detail.getProperties()).containsKey("timestamp");
    assertThat(detail.getProperties()).containsKey("errors");
  }

  static class DummyController {
    void create(@Valid Payload payload) {}
  }

  record Payload(String name) {}
}
