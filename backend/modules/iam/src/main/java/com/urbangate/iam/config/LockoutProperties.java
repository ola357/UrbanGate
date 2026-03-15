// Copyright (c) UrbanGate
package com.urbangate.iam.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "urbangate.security.lockout")
public class LockoutProperties {
  private Duration window = Duration.ofMinutes(30);
  private int maxFailures = 3;

  public Duration getWindow() {
    return window;
  }

  public void setWindow(Duration window) {
    this.window = window;
  }

  public int getMaxFailures() {
    return maxFailures;
  }

  public void setMaxFailures(int maxFailures) {
    this.maxFailures = maxFailures;
  }
}
