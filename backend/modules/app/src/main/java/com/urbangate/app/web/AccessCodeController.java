// Copyright (c) UrbanGate
package com.urbangate.app.web;

import com.urbangate.access.dto.request.AccessCodeRequest;
import com.urbangate.access.dto.response.AccessCodeResponse;
import com.urbangate.access.service.AccessCodeService;
import com.urbangate.shared.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
public class AccessCodeController {

  private final AccessCodeService accessCodeService;

  @PostMapping
  @PreAuthorize("hasRole('RESIDENT')")
  public ResponseEntity<ApiResponse<AccessCodeResponse>> createAccessCode(
      @Valid @RequestBody AccessCodeRequest request,
      @AuthenticationPrincipal Jwt principal,
      HttpServletRequest httpServletRequest) {
    AccessCodeResponse accessCodeResponse =
        accessCodeService.createAccessCode(request, principal, httpServletRequest);
    return ResponseEntity.ok(new ApiResponse<AccessCodeResponse>().success(accessCodeResponse));
  }

  @PatchMapping("/{accessCode}")
  @PreAuthorize("hasRole('RESIDENT')")
  public ResponseEntity<ApiResponse<String>> extendExpirationTimeForAccessCode(
      @PathVariable(value = "accessCode") String accessCode,
      @RequestParam(value = "expiryTime") String expiryTime) {

    String accessCodeResponse =
        accessCodeService.extendExpirationTimeForAccessCode(accessCode, expiryTime);
    return ResponseEntity.ok(new ApiResponse<String>().success(accessCodeResponse));
  }

  @PatchMapping("/{accessCode}/revoke")
  @PreAuthorize("hasRole('RESIDENT')")
  public ResponseEntity<ApiResponse<String>> revokeAccessCode(
      @PathVariable(value = "accessCode") String accessCode) {
    String accessCodeResponse = accessCodeService.revokeAccessCode(accessCode);
    return ResponseEntity.ok(new ApiResponse<String>().success(accessCodeResponse));
  }

  @GetMapping("codes")
  @PreAuthorize("hasRole('RESIDENT')")
  public ResponseEntity<ApiResponse<List<AccessCodeResponse>>> retrieveAllAccessCodesByUser(
      @AuthenticationPrincipal Jwt principal) {
    List<AccessCodeResponse> accessCodeResponse = accessCodeService.getAccessCodesByUser(principal);
    return ResponseEntity.ok(
        new ApiResponse<List<AccessCodeResponse>>().success(accessCodeResponse));
  }
}
