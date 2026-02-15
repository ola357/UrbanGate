package com.urbangate.app.config;

import com.urbangate.shared.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
    var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Validation error");
    pd.setDetail("One or more fields are invalid.");
    pd.setProperty("code", ErrorCode.VALIDATION_ERROR.name());
    pd.setProperty("timestamp", Instant.now().toString());
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
    pd.setProperty("timestamp", Instant.now().toString());
    pd.setProperty("path", request.getRequestURI());
    return pd;
  }
}
