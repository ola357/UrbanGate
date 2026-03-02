// Copyright (c) UrbanGate
package com.urbangate.iam.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KeycloakAdminClientConfig {

  private final KeycloakProperties properties;

  @Bean
  public Keycloak keycloakAdminClient() {
    log.info("Initializing Keycloak Admin Client...");
    return KeycloakBuilder.builder()
        .serverUrl(properties.getServerUrl())
        .realm(properties.getAdminRealm())
        .grantType(OAuth2Constants.PASSWORD)
        .clientId(properties.getAdminClientId())
        .username(properties.getAdminUsername())
        .password(properties.getAdminPassword())
        .build();
  }
}
