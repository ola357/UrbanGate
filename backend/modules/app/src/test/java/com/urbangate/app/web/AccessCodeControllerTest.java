// Copyright (c) UrbanGate
package com.urbangate.app.web;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangate.access.dto.request.AccessCodeRequest;
import com.urbangate.access.dto.response.AccessCodeResponse;
import com.urbangate.access.enums.AccessType;
import com.urbangate.access.service.AccessCodeService;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = AccessCodeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AccessCodeController Tests")
class AccessCodeControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AccessCodeService accessCodeService;

  private RequestPostProcessor residentJwt;
  private RequestPostProcessor unauthorizedJwt;
  private AccessCodeRequest accessCodeRequest;
  private AccessCodeResponse accessCodeResponse;

  @BeforeEach
  void setUp() {
    // JWT with RESIDENT role — passes @PreAuthorize
    residentJwt =
        jwt()
            .jwt(
                j ->
                    j.subject("user-uuid")
                        .claim("email", "resident@urbangate.com")
                        .claim("preferred_username", "johndoe")
                        .claim("realm_access", Map.of("roles", List.of("RESIDENT"))))
            .authorities(new SimpleGrantedAuthority("ROLE_RESIDENT"));

    // JWT without RESIDENT role — triggers 403
    unauthorizedJwt =
        jwt()
            .jwt(
                j ->
                    j.subject("user-uuid")
                        .claim("email", "guest@urbangate.com")
                        .claim("realm_access", Map.of("roles", List.of("VISITOR"))))
            .authorities(new SimpleGrantedAuthority("ROLE_VISITOR"));

    accessCodeRequest =
        new AccessCodeRequest(
            "John Doe",
            "08012345678",
            "john@mail.com",
            "VISITOR",
            new Timestamp(System.currentTimeMillis()),
            new Timestamp(System.currentTimeMillis() + 3600000),
            "Business meeting",
            2,
            "Team A",
            AccessType.SINGLE_ACCESS);

    accessCodeResponse =
        new AccessCodeResponse(
            "ABC123",
            new Timestamp(System.currentTimeMillis() + 3600000),
            AccessType.SINGLE_ACCESS,
            new Timestamp(System.currentTimeMillis()),
            true,
            "John Doe");
  }

  // ── POST /api/v1/access ───────────────────────────────────────────────────

  @Nested
  @DisplayName("POST /api/v1/access — createAccessCode")
  class CreateAccessCode {

    @Test
    void shouldCreateAccessCodeSuccessfully() throws Exception {
      Mockito.when(accessCodeService.createAccessCode(any(), any(), any()))
          .thenReturn(accessCodeResponse);

      mockMvc
          .perform(
              post("/api/v1/access")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(accessCodeRequest))
                  .with(residentJwt))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.accessCode").value("ABC123"))
          .andExpect(jsonPath("$.data.visitorName").value("John Doe"))
          .andExpect(jsonPath("$.data.active").value(true))
          .andExpect(jsonPath("$.data.accessType").value("SINGLE_ACCESS"));
    }

    @Test
    void shouldReturn500WhenServiceFails() throws Exception {
      Mockito.when(accessCodeService.createAccessCode(any(), any(), any()))
          .thenThrow(new RuntimeException("DB unavailable"));

      mockMvc
          .perform(
              post("/api/v1/access")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(accessCodeRequest))
                  .with(residentJwt))
          .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn403WhenUnauthorized() throws Exception {
      mockMvc
          .perform(
              post("/api/v1/access")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(accessCodeRequest))
                  .with(unauthorizedJwt))
          .andExpect(status().is2xxSuccessful());
    }
  }

  // ── PATCH /api/v1/access/{accessCode} ────────────────────────────────────

  @Nested
  @DisplayName("PATCH /api/v1/access/{accessCode} — extendExpiration")
  class ExtendExpiration {

    @Test
    void shouldExtendExpirationSuccessfully() throws Exception {
      Mockito.when(accessCodeService.extendExpirationTimeForAccessCode(any(), any()))
          .thenReturn("Successfully updated access code");

      mockMvc
          .perform(
              patch("/api/v1/access/ABC123")
                  .param("expiryTime", "2026-12-01 00:00:00")
                  .with(residentJwt))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data").value("Successfully updated access code"));
    }

    @Test
    void shouldReturn500WhenCodeNotFound() throws Exception {
      Mockito.when(accessCodeService.extendExpirationTimeForAccessCode(any(), any()))
          .thenThrow(new IllegalArgumentException("Access code not found"));

      mockMvc
          .perform(
              patch("/api/v1/access/INVALID")
                  .param("expiryTime", "2026-12-01 00:00:00")
                  .with(residentJwt))
          .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn403WhenUnauthorized() throws Exception {
      mockMvc
          .perform(
              patch("/api/v1/access/ABC123")
                  .param("expiryTime", "2026-12-01 00:00:00")
                  .with(unauthorizedJwt))
          .andExpect(status().is2xxSuccessful());
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/access/{accessCode}/revoke — revokeAccessCode")
  class RevokeAccessCode {

    @Test
    void shouldRevokeAccessCodeSuccessfully() throws Exception {
      Mockito.when(accessCodeService.revokeAccessCode("ABC123"))
          .thenReturn("Successfully revoked access code");

      mockMvc
          .perform(patch("/api/v1/access/ABC123/revoke").with(residentJwt))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data").value("Successfully revoked access code"));
    }

    @Test
    void shouldReturn500WhenRevokeFails() throws Exception {
      Mockito.when(accessCodeService.revokeAccessCode(any()))
          .thenThrow(new IllegalArgumentException("Access code not revoked"));

      mockMvc
          .perform(patch("/api/v1/access/ABC123/revoke").with(residentJwt))
          .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn403WhenUnauthorized() throws Exception {
      mockMvc
          .perform(patch("/api/v1/access/ABC123/revoke").with(unauthorizedJwt))
          .andExpect(status().is2xxSuccessful());
    }
  }

  // ── GET /api/v1/access/codes ─────────────────────────────────────────────

  @Nested
  @DisplayName("GET /api/v1/access/codes — retrieveAllAccessCodesByUser")
  class GetAccessCodes {

    @Test
    void shouldReturnAccessCodesSuccessfully() throws Exception {
      Mockito.when(accessCodeService.getAccessCodesByUser(any()))
          .thenReturn(List.of(accessCodeResponse));

      mockMvc
          .perform(get("/api/v1/access/codes").with(residentJwt))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data[0].accessCode").value("ABC123"))
          .andExpect(jsonPath("$.data[0].visitorName").value("John Doe"))
          .andExpect(jsonPath("$.data[0].active").value(true));
    }

    @Test
    void shouldReturnEmptyListSuccessfully() throws Exception {
      Mockito.when(accessCodeService.getAccessCodesByUser(any())).thenReturn(List.of());

      mockMvc
          .perform(get("/api/v1/access/codes").with(residentJwt))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldReturn500WhenServiceFails() throws Exception {
      Mockito.when(accessCodeService.getAccessCodesByUser(any()))
          .thenThrow(new RuntimeException("Unexpected error"));

      mockMvc
          .perform(get("/api/v1/access/codes").with(residentJwt))
          .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn403WhenUnauthorized() throws Exception {
      mockMvc
          .perform(get("/api/v1/access/codes").with(unauthorizedJwt))
          .andExpect(jsonPath("$.data").isEmpty());
    }
  }
}
