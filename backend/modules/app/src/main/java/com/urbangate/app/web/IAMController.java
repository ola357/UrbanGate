// Copyright (c) UrbanGate
package com.urbangate.app.web;

import com.urbangate.iam.dto.request.ActivationRequest;
import com.urbangate.iam.dto.request.AuthRequest;
import com.urbangate.iam.dto.request.PasswordRequest;
import com.urbangate.iam.dto.request.RefreshTokenRequest;
import com.urbangate.iam.dto.request.ResidentOnboardingRequest;
import com.urbangate.iam.dto.response.PasswordResponse;
import com.urbangate.iam.dto.response.ResidentOnboardingResponse;
import com.urbangate.iam.dto.response.TokenResponse;
import com.urbangate.iam.dto.response.UserResponse;
import com.urbangate.iam.service.KeyCloakTokenService;
import com.urbangate.iam.service.KeycloakUserService;
import com.urbangate.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class IAMController {

  private final KeyCloakTokenService tokenService;
  private final KeycloakUserService userService;

  @PostMapping("/login")
  @Operation(
      summary = "Sign in",
      description =
          "Authenticate with username/email and password. Returns JWT access token and refresh token.")
  public ResponseEntity<ApiResponse<TokenResponse>> login(
      @Valid @RequestBody AuthRequest request,
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) {

    TokenResponse tokens = tokenService.login(request, httpServletRequest);
    return ResponseEntity.ok(new ApiResponse<TokenResponse>().success(tokens));
  }

  @PostMapping("/register")
  @Operation(
      summary = "Register new user",
      description =
          "Create a new user account. Sends a verification email. Default role is 'user'.")
  public ResponseEntity<ApiResponse<ResidentOnboardingResponse>> register(
      @Valid @RequestBody ResidentOnboardingRequest request,
      HttpServletRequest httpServletRequest) {
    ResidentOnboardingResponse user = userService.register(request, httpServletRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse<ResidentOnboardingResponse>().success(user));
  }

  @Operation(
      summary = "Refresh access token",
      description =
          "Use a valid refresh token to obtain a new access token without re-authenticating.")
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<TokenResponse>> refresh(
      @Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpServletRequest) {
    TokenResponse tokens = tokenService.refresh(request.refreshToken(), httpServletRequest);
    return ResponseEntity.ok(new ApiResponse<TokenResponse>().success(tokens));
  }

  @Operation(
      summary = "Confirm Registration Details",
      description = "Retrieve User Details For Confirmation.")
  @PostMapping("/confirm")
  public ResponseEntity<ApiResponse<UserResponse>> confirmUserDetails(
      @Valid @RequestBody ActivationRequest request) {
    UserResponse userResponse = userService.getUserByActivationCode(request.token());
    return ResponseEntity.ok(new ApiResponse<UserResponse>().success(userResponse));
  }

  @Operation(summary = "Set-Up New Password", description = "Create Password for a new User")
  @PostMapping("/password-set-up")
  public ResponseEntity<ApiResponse<PasswordResponse>> setUpPassword(
      @Valid @RequestBody PasswordRequest request) {
    PasswordResponse userResponse = userService.setUpPassword(request);
    return ResponseEntity.ok(new ApiResponse<PasswordResponse>().success(userResponse));
  }
}
