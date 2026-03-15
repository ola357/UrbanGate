// Copyright (c) UrbanGate
package com.urbangate.shared.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class CurrentUserTest {

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void returnsEmptyWhenNoAuthentication() {
    assertFalse(CurrentUser.jwt().isPresent());
    assertFalse(CurrentUser.subject().isPresent());
    assertFalse(CurrentUser.email().isPresent());
    assertFalse(CurrentUser.displayName().isPresent());
  }

  @Test
  void readsClaimsFromJwt() {
    Jwt jwt =
        new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            Map.of(
                "sub", "user-123",
                "email", "user@example.com",
                "name", "Test User"));

    SecurityContextHolder.getContext().setAuthentication(jwtAuth(jwt));

    assertEquals("user-123", CurrentUser.subject().orElseThrow());
    assertEquals("user@example.com", CurrentUser.email().orElseThrow());
    assertEquals("Test User", CurrentUser.displayName().orElseThrow());
  }

  @Test
  void fallsBackToPreferredUsernameAndGivenFamily() {
    Jwt jwt =
        new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            Map.of(
                "sub", "user-456",
                "preferred_username", "fallback@example.com",
                "given_name", "Jane",
                "family_name", "Doe"));

    SecurityContextHolder.getContext().setAuthentication(jwtAuth(jwt));

    assertEquals("fallback@example.com", CurrentUser.email().orElseThrow());
    assertEquals("Jane Doe", CurrentUser.displayName().orElseThrow());
  }

  private Authentication jwtAuth(Jwt jwt) {
    AbstractAuthenticationToken auth =
        new AbstractAuthenticationToken(List.of()) {
          @Override
          public Object getCredentials() {
            return "";
          }

          @Override
          public Object getPrincipal() {
            return jwt;
          }
        };
    auth.setAuthenticated(true);
    return auth;
  }
}
