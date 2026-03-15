// Copyright (c) UrbanGate
package com.urbangate.iam.config;

import com.urbangate.iam.keycloak.KeycloakAdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfiguration {

  @Bean
  public KeycloakAdminClient keycloakAdminClient(KeycloakAdminProperties properties) {
    return new KeycloakAdminClient(properties);
  }
}
