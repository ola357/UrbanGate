// Copyright (c) UrbanGate
package com.urbangate.app.config;

import com.urbangate.shared.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  private static final String TIMESTAMP_PROPERTY = "timestamp";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Validation error");
    pd.setDetail("One or more fields are invalid.");
    pd.setProperty("code", ErrorCode.VALIDATION_ERROR.name());
    pd.setProperty(TIMESTAMP_PROPERTY, Instant.now().toString());
    pd.setProperty("path", request.getRequestURI());
    pd.setProperty(
        "errors",
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
            .toList());
    return pd;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {
    var pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    pd.setTitle("Internal error");
    pd.setDetail("Unexpected error.");
    pd.setProperty("code", ErrorCode.INTERNAL_ERROR.name());
    pd.setProperty(TIMESTAMP_PROPERTY, Instant.now().toString());
    pd.setProperty("path", request.getRequestURI());
    return pd;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
    return buildProblemDetail(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(), request);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ProblemDetail handleConflict(IllegalStateException ex, HttpServletRequest request) {
    return buildProblemDetail(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
  }

  private ProblemDetail buildProblemDetail(
      HttpStatusCode status, String title, String detail, HttpServletRequest request) {
    var pd = ProblemDetail.forStatus(status);
    pd.setTitle(title);
    pd.setDetail(detail);
    pd.setProperty(TIMESTAMP_PROPERTY, Instant.now().toString());
    pd.setProperty("path", request.getRequestURI());
    return pd;
  }
}
