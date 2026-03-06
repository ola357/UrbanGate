
package com.urbangate.app.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangate.iam.dto.request.AuthRequest;
import com.urbangate.iam.dto.request.PasswordRequest;
import com.urbangate.iam.dto.request.ResidentOnboardingRequest;
import com.urbangate.iam.dto.response.PasswordResponse;
import com.urbangate.iam.dto.response.ResidentOnboardingResponse;
import com.urbangate.iam.dto.response.TokenResponse;
import com.urbangate.iam.dto.response.UserResponse;
import com.urbangate.iam.service.KeyCloakTokenService;
import com.urbangate.iam.service.KeycloakUserService;
import com.urbangate.shared.exceptions.AuthenticationException;
import com.urbangate.shared.exceptions.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Import(IAMControllerTest.TestSecurityConfig.class)
@WebMvcTest(IAMController.class)
class IAMControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private KeyCloakTokenService tokenService;

  @MockitoBean private KeycloakUserService userService;
  private static final String LOGIN_URL = "/api/v1/auth/login";
  private static final String TENANT = "avera";

  private AuthRequest validRequest;
  private TokenResponse mockTokenResponse;
  private ResidentOnboardingRequest validRegistrationRequest;
  private ResidentOnboardingResponse registrationResponse;
  private UserResponse userResponse;
  private PasswordRequest validPasswordRequest;
  private PasswordResponse passwordResponse;

  @BeforeEach
  void setUp() {
    validRequest = new AuthRequest("09012006030", "SecurePass123!", "avera");

    mockTokenResponse =
        new TokenResponse(
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.mock-access-token",
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.mock-refresh-token",
            "Bearer",
            "profile");

    validRegistrationRequest =
        new ResidentOnboardingRequest(
            "09012006030", "vincent@example.com", "Vincent", "Enwere", "M", "admin", "M");

    registrationResponse =
        new ResidentOnboardingResponse("683127b5-4432-418c-962b-97c3ea5bfe23", "09012006030");


    // Password setup
    validPasswordRequest =
        new PasswordRequest(
            "NewSecurePass123!",
            "683127b5-4432-418c-962b-97c3ea5bfe23",
            "valid-activation-token-abc123");
    passwordResponse = new PasswordResponse("683127b5-4432-418c-962b-97c3ea5bfe23", true);
  }

  @TestConfiguration
  @EnableWebSecurity
  static class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(
              auth -> auth.anyRequest().permitAll() // ← permit all in tests
              );
      return http.build();
    }
  }

  @Nested
  @DisplayName("Successful login")
  class SuccessfulLogin {

    @Test
    @DisplayName("returns 200 with tokens and user info on valid credentials")
    void login_validCredentials_returns200WithTokens() throws Exception {
      given(tokenService.login(any(AuthRequest.class), any())).willReturn(mockTokenResponse);

      performLogin(validRequest)
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
          .andExpect(jsonPath("$.message").value("success"))
          .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
          .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("calls tokenService exactly once with the correct request")
    void login_validCredentials_callsServiceOnce() throws Exception {
      given(tokenService.login(any(AuthRequest.class), any())).willReturn(mockTokenResponse);

      performLogin(validRequest);

      verify(tokenService, times(1)).login(any(AuthRequest.class), any());
    }
  }

  @Test
  @DisplayName("returns 401 when credentials are wrong")
  void login_wrongPassword_returns401() throws Exception {
    given(tokenService.login(any(AuthRequest.class), any()))
        .willThrow(new AuthenticationException("Invalid user credentials"));

    performLogin(new AuthRequest("09012006030", "WrongPass!", "realm"))
        .andDo(print())
        .andExpect(jsonPath("$.detail").value("Invalid user credentials"));
  }

  @Test
  @DisplayName("returns 401 when user does not exist")
  void login_unknownUser_returns401() throws Exception {
    given(tokenService.login(any(AuthRequest.class), any()))
        .willThrow(new AuthenticationException("User not found"));

    performLogin(new AuthRequest("unknown_user", "AnyPass123!", "realm"))
        .andExpect(jsonPath("$.detail").value("User not found"));
  }

  @Nested
  @DisplayName("POST /api/v1/auth/register")
  class RegisterEndpoint {

    @Test
    @DisplayName("✅ returns 201 with user details on valid registration")
    void register_validRequest_returns201() throws Exception {
      given(userService.register(any(ResidentOnboardingRequest.class), any()))
          .willReturn(registrationResponse);

      performPost("/api/v1/auth/register", validRegistrationRequest)
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.message").value("success"))
          .andExpect(jsonPath("$.data.userId").value("683127b5-4432-418c-962b-97c3ea5bfe23"))
          .andExpect(jsonPath("$.data.activationToken").value("09012006030"));
    }

    @Test
    @DisplayName("❌ error when phone number already exists")
    void register_duplicatePhoneNumber_returns409() throws Exception {
      given(userService.register(any(ResidentOnboardingRequest.class), any()))
          .willThrow(new ConflictException("Phone number already registered"));

      performPost("/api/v1/auth/register", validRegistrationRequest)
          .andDo(print())
          .andExpect(jsonPath("$.detail").value("Phone number already registered"));
    }
  }

  private ResultActions performLogin(AuthRequest request) throws Exception {
    return mockMvc.perform(
        post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Tenant-ID", "avera") // required by TenantResolutionFilter
            .content(objectMapper.writeValueAsString(request)));
  }

  private ResultActions performPost(String url, Object body) throws Exception {
    return mockMvc.perform(
        post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Tenant-ID", TENANT)
            .content(objectMapper.writeValueAsString(body)));
  }
}
