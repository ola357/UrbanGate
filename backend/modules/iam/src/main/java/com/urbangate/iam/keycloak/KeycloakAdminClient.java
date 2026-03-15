// Copyright (c) UrbanGate
package com.urbangate.iam.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.urbangate.iam.config.KeycloakAdminProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

public class KeycloakAdminClient {
  private static final Duration TOKEN_SKEW = Duration.ofSeconds(30);

  private final KeycloakAdminProperties properties;
  private final RestClient restClient;
  private final AtomicReference<TokenCache> tokenCache = new AtomicReference<>();

  public KeycloakAdminClient(KeycloakAdminProperties properties) {
    this.properties = properties;
    this.restClient = RestClient.builder().build();
  }

  public void updatePassword(String keycloakUserId, String newPassword) {
    ensureEnabled();
    if (keycloakUserId == null || keycloakUserId.isBlank()) {
      throw new IllegalArgumentException("Keycloak user id is required");
    }

    String token = getAccessToken();
    String baseUrl = normalizeBaseUrl(properties.getBaseUrl());
    String realm = properties.getRealm();

    restClient
        .put()
        .uri("%s/admin/realms/%s/users/%s/reset-password".formatted(baseUrl, realm, keycloakUserId))
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .body(Map.of("type", "password", "value", newPassword, "temporary", false))
        .retrieve()
        .toBodilessEntity();
  }

  private void ensureEnabled() {
    if (!properties.isEnabled()) {
      throw new IllegalStateException("Keycloak admin client is disabled");
    }
    if (isBlank(properties.getBaseUrl())
        || isBlank(properties.getRealm())
        || isBlank(properties.getClientId())
        || isBlank(properties.getClientSecret())) {
      throw new IllegalStateException("Keycloak admin properties are not fully configured");
    }
  }

  private String getAccessToken() {
    TokenCache cached = tokenCache.get();
    Instant now = Instant.now();
    if (cached != null && cached.expiresAt().isAfter(now.plus(TOKEN_SKEW))) {
      return cached.token();
    }

    TokenResponse response = requestToken();
    Instant expiresAt = now.plusSeconds(response.expiresIn());
    tokenCache.set(new TokenCache(response.accessToken(), expiresAt));
    return response.accessToken();
  }

  private TokenResponse requestToken() {
    String baseUrl = normalizeBaseUrl(properties.getBaseUrl());
    String realm = properties.getRealm();

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "client_credentials");
    body.add("client_id", properties.getClientId());
    body.add("client_secret", properties.getClientSecret());

    return restClient
        .post()
        .uri("%s/realms/%s/protocol/openid-connect/token".formatted(baseUrl, realm))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(body)
        .retrieve()
        .body(TokenResponse.class);
  }

  private static String normalizeBaseUrl(String baseUrl) {
    if (baseUrl == null) {
      return "";
    }
    return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private record TokenCache(String token, Instant expiresAt) {}

  private record TokenResponse(
      @JsonProperty("access_token") String accessToken,
      @JsonProperty("expires_in") long expiresIn) {}
}
