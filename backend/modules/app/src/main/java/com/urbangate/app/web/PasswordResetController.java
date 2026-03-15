// Copyright (c) UrbanGate
package com.urbangate.app.web;

import com.urbangate.iam.service.PasswordResetCode;
import com.urbangate.iam.service.PasswordResetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/onboarding/password-resets")
public class PasswordResetController {
  private final PasswordResetService passwordResetService;

  public PasswordResetController(PasswordResetService passwordResetService) {
    this.passwordResetService = passwordResetService;
  }

  @PostMapping
  public ResponseEntity<PasswordResetResponse> issueResetCode(
      @RequestBody @Valid PasswordResetRequest request) {
    PasswordResetCode code = resolveIssue(request);
    return ResponseEntity.ok(new PasswordResetResponse(code.resetId(), code.expiresAt()));
  }

  @PostMapping("/confirm")
  public ResponseEntity<Void> confirmReset(
      @RequestBody @Valid PasswordResetConfirmRequest request) {
    resolveConfirm(request);
    return ResponseEntity.noContent().build();
  }

  private PasswordResetCode resolveIssue(PasswordResetRequest request) {
    if (request.residentId() != null) {
      return passwordResetService.issueResetCode(request.residentId());
    }
    if (request.phone() != null && !request.phone().isBlank()) {
      return passwordResetService.issueResetCodeForPhone(request.phone());
    }
    throw new IllegalArgumentException("Either residentId or phone is required");
  }

  private void resolveConfirm(PasswordResetConfirmRequest request) {
    if (request.residentId() != null) {
      passwordResetService.confirmReset(
          request.residentId(), request.code(), request.newPassword());
      return;
    }
    if (request.phone() != null && !request.phone().isBlank()) {
      passwordResetService.confirmResetForPhone(
          request.phone(), request.code(), request.newPassword());
      return;
    }
    throw new IllegalArgumentException("Either residentId or phone is required");
  }

  public record PasswordResetRequest(UUID residentId, String phone) {}

  public record PasswordResetResponse(UUID resetId, Instant expiresAt) {}

  public record PasswordResetConfirmRequest(
      UUID residentId, String phone, @NotBlank String code, @NotBlank String newPassword) {}
}
