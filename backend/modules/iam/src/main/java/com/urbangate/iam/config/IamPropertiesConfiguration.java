// Copyright (c) UrbanGate
package com.urbangate.iam.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  ActivationCodeProperties.class,
  PasswordResetProperties.class,
  LockoutProperties.class,
  KeycloakAdminProperties.class
})
public class IamPropertiesConfiguration {}
