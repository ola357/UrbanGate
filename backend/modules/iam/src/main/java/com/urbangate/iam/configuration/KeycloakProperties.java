// Copyright (c) UrbanGate
package com.urbangate.iam.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

  private String url;
  private String adminClientId;
  private String adminPassword;
  private String adminUsername;
  private String publicClientId;
  private String adminRealm;
  private String redirectUri;
  private String trustedOrigin;

  public String getTokenEndpoint(String realm) {
    return String.format("%s/realms/%s/protocol/openid-connect/token", url, realm);
  }

  public String getServerUrl() {
    return url;
  }
}
