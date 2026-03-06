package com.urbangate.app.web;


import com.urbangate.iam.dto.request.ResetPasswordRequest;
import com.urbangate.iam.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class PasswordRestController {

  private final PasswordResetService resetService;

  @PostMapping("/forgot-password")
  @PreAuthorize("hasAnyRole('ADMIN', 'RESIDENT')")
  public ResponseEntity<String> forgotPassword(@RequestParam String email) {
    resetService.initiatePasswordReset(email);
    return ResponseEntity.ok("If your email is registered, a reset code has been sent.");
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'RESIDENT')")
  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
    resetService.confirmPasswordReset(
            request.email(),
            request.code(),
            request.password()
    );
    return ResponseEntity.ok("Password updated successfully.");
  }
}
