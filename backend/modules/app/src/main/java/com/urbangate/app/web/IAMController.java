
package com.urbangate.app.web;

import com.urbangate.iam.dto.request.*;
import com.urbangate.iam.dto.response.PasswordResponse;
import com.urbangate.iam.dto.response.ResidentOnboardingResponse;
import com.urbangate.iam.dto.response.TokenResponse;
import com.urbangate.iam.dto.response.UserResponse;
import com.urbangate.iam.service.KeycloakTokenService;
import com.urbangate.iam.service.KeycloakUserService;
import com.urbangate.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  private final KeycloakTokenService tokenService;
  private final KeycloakUserService userService;


  @PostMapping("/login")
  @Operation(
      summary = "Sign in",
      description =
          "Authenticate with username/email and password. Returns JWT access token and refresh token.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Successfully authenticated"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid credentials")
  })
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
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "User created successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Username or email already exists"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation error")
  })
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

  //    // ── Logout ─────────────────────────────────────────────────────────────────
  //
  //    @PostMapping("/logout")
  //    @Operation(
  //            summary = "Logout",
  //            description = "Revoke the refresh token. The access token will expire naturally
  // (short-lived).",
  //            security = @SecurityRequirement(name = "Keycloak")
  //    )
  //    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest
  // request) {
  //        tokenService.logout(request.getRefreshToken());
  //        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
  //    }
  //
  //    // ── Forgot Password ────────────────────────────────────────────────────────
  //
  //    @PostMapping("/forgot-password")
  //    @Operation(
  //            summary = "Forgot password",
  //            description = "Triggers a password-reset email from Keycloak. The email contains a
  // secure link. " +
  //                    "Always returns 200 to prevent email enumeration attacks."
  //    )
  //    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody
  // ForgotPasswordRequest request) {
  //        // Service handles the case where email doesn't exist silently
  //        userService.sendPasswordResetEmail(request.getEmail());
  //        return ResponseEntity.ok(ApiResponse.ok(
  //                "If this email is registered, a password reset link has been sent."));
  //    }
  //
  //    // ── Reset Password (token flow) ────────────────────────────────────────────
  //
  //    @PostMapping("/reset-password")
  //    @Operation(
  //            summary = "Reset password",
  //            description = "Set a new password using the userId extracted from the Keycloak reset
  // email. " +
  //                    "In production, integrate with Keycloak's action token mechanism for full
  // security."
  //    )
  //    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody
  // ResetPasswordRequest request) {
  //        // token format: userId (provided from the Keycloak reset link redirect)
  //        String userId = request.getToken(); // Simplified; in prod use Keycloak action token
  //        userService.adminResetPassword(userId, request.getNewPassword());
  //        return ResponseEntity.ok(ApiResponse.ok("Password reset successfully. Please log in."));
  //    }
  //
  //    // ── Change Password (authenticated) ───────────────────────────────────────
  //
  //    @PutMapping("/change-password")
  //    @Operation(
  //            summary = "Change password (authenticated)",
  //            description = "Change password for the currently logged-in user.",
  //            security = @SecurityRequirement(name = "Keycloak")
  //    )
  //    public ResponseEntity<ApiResponse<Void>> changePassword(
  //            @AuthenticationPrincipal Jwt jwt,
  //            @Valid @RequestBody ChangePasswordRequest request
  //    ) {
  //        String userId = jwt.getSubject();
  //        // Validate current password by attempting a login
  //        LoginRequest loginCheck = new LoginRequest();
  //        loginCheck.setUsername(jwt.getClaim("preferred_username"));
  //        loginCheck.setPassword(request.getCurrentPassword());
  //        tokenService.login(loginCheck); // Throws if credentials are wrong
  //
  //        userService.resetPassword(userId, request.getNewPassword(), false);
  //        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully"));
  //    }
  //
  //    // ── Me ─────────────────────────────────────────────────────────────────────
  //
  //    @GetMapping("/me")
  //    @Operation(
  //            summary = "Get current user profile",
  //            description = "Returns the profile of the currently authenticated user.",
  //            security = @SecurityRequirement(name = "Keycloak")
  //    )
  //    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal Jwt jwt) {
  //        UserResponse user = userService.getUserById(jwt.getSubject());
  //        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved", user));
  //    }
}
