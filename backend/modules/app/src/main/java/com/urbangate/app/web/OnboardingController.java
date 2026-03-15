// Copyright (c) UrbanGate
package com.urbangate.app.web;

import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.service.OnboardingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/onboarding")
public class OnboardingController {
  private final OnboardingService onboardingService;

  public OnboardingController(OnboardingService onboardingService) {
    this.onboardingService = onboardingService;
  }

  @PostMapping("/activation-codes")
  public ResponseEntity<ActivationCodeResponse> issueActivationCode(
      @RequestBody @Valid IssueActivationCodeRequest request) {
    var code = onboardingService.issueActivationCode(request.residentId());
    return ResponseEntity.ok(new ActivationCodeResponse(code.getCode(), code.getExpiresAt()));
  }

  @PostMapping("/activate")
  public ResponseEntity<ResidentActivationResponse> activateResident(
      @RequestBody @Valid ActivateResidentRequest request) {
    Resident resident =
        onboardingService.activateResident(request.activationCode(), request.keycloakUserId());
    return ResponseEntity.ok(
        new ResidentActivationResponse(
            resident.getId(), resident.getStatus().name(), resident.getActivatedAt()));
  }

  public record IssueActivationCodeRequest(@NotNull UUID residentId) {}

  public record ActivationCodeResponse(String code, Instant expiresAt) {}

  public record ActivateResidentRequest(@NotBlank String activationCode, String keycloakUserId) {}

  public record ResidentActivationResponse(UUID residentId, String status, Instant activatedAt) {}
}
