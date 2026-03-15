// Copyright (c) UrbanGate
package com.urbangate.app.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "urbangate.security")
public class UrbangateSecurityProperties {
  private boolean enabled = false;
  private String tenantClaim = "estate_id";
  private String tenantHeader = "X-Estate-Id";
  private boolean allowHeaderTenant = false;
  private String rolesClaim = "realm_access.roles";
  private String resourceClientId = "";
}
