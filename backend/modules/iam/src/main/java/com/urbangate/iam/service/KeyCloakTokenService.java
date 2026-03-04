// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import static com.urbangate.iam.util.TenantResolutionFilter.resolveTenant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangate.iam.configuration.KeycloakProperties;
import com.urbangate.iam.dto.request.AuthRequest;
import com.urbangate.iam.dto.response.TokenResponse;
import com.urbangate.shared.exceptions.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyCloakTokenService {

  private final KeycloakProperties keycloakProperties;
  private final ObjectMapper objectMapper;
  private static final String TENANT_PREFIX = "tenant_";
  private static final String REFRESH_TOKEN_KEY = "refresh_token";
  private static final String CLIENT_ID_KEY = "client_id";

  public TokenResponse login(AuthRequest request, HttpServletRequest httpServletRequest)
      throws AuthenticationException {
    log.info("Login attempt for user: {}", request.phoneNumber());

    String realm = TENANT_PREFIX + resolveTenant(httpServletRequest);

    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("grant_type", "password"));
    params.add(new BasicNameValuePair(CLIENT_ID_KEY, keycloakProperties.getPublicClientId()));
    params.add(new BasicNameValuePair("username", request.phoneNumber()));
    params.add(new BasicNameValuePair("password", request.password()));
    params.add(new BasicNameValuePair("scope", "openid profile email roles"));

    JsonNode tokenResponse = callTokenEndpoint(params, realm);
    return buildAuthResponse(tokenResponse);
  }

  public TokenResponse refresh(String refreshToken, HttpServletRequest httpServletRequest)
      throws AuthenticationException {
    log.info("Refreshing token");

    String realm = TENANT_PREFIX + resolveTenant(httpServletRequest);
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("grant_type", REFRESH_TOKEN_KEY));
    params.add(new BasicNameValuePair(CLIENT_ID_KEY, keycloakProperties.getPublicClientId()));
    params.add(new BasicNameValuePair(REFRESH_TOKEN_KEY, refreshToken));

    JsonNode tokenResponse = callTokenEndpoint(params, realm);
    return buildAuthResponse(tokenResponse);
  }

  public void logout(String refreshToken, HttpServletRequest httpServletRequest)
      throws AuthenticationException {
    log.info("Revoking refresh token");
    String realm = TENANT_PREFIX + resolveTenant(httpServletRequest);
    String logoutUrl =
        String.format(
            "%s/realms/%s/protocol/openid-connect/logout", keycloakProperties.getUrl(), realm);

    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair(CLIENT_ID_KEY, keycloakProperties.getAdminClientId()));
    params.add(new BasicNameValuePair(REFRESH_TOKEN_KEY, refreshToken));

    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpPost post = new HttpPost(logoutUrl);
      post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
      client.execute(
          post,
          response -> {
            log.info("Logout response status: {}", response.getCode());
            return null;
          });
    } catch (IOException e) {
      log.warn("Logout request failed: {}", e.getMessage());
    }
  }

  private JsonNode callTokenEndpoint(List<NameValuePair> params, String realm) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpPost post = new HttpPost(keycloakProperties.getTokenEndpoint(realm));
      post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

      return client.execute(
          post,
          response -> {
            String body =
                new String(
                    response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            JsonNode json = objectMapper.readTree(body);

            if (response.getCode() != 200) {
              String error =
                  json.path("error_description")
                      .asText(json.path("error").asText("Authentication failed"));
              log.warn("Token endpoint error {}: {}", response.getCode(), error);
              throw new AuthenticationException(error);
            }
            return json;
          });

    } catch (AuthenticationException e) {
      throw e;
    } catch (IOException e) {
      log.error("Token endpoint connection failed", e);
      throw new AuthenticationException("Unable to connect to authentication service");
    }
  }

  private TokenResponse buildAuthResponse(JsonNode json) {
    String accessToken = json.path("access_token").asText();
    String refreshToken = json.path(REFRESH_TOKEN_KEY).asText(null);
    String scope = json.path("scope").asText(null);
    String expiresIn = json.path("expires_in").asText(null);

    return new TokenResponse(accessToken, refreshToken, expiresIn, scope);
  }
}
