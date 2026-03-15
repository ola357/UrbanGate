// Copyright (c) UrbanGate
package com.urbangate.iam.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "urbangate.onboarding.activation-code")
public class ActivationCodeProperties {
  private Duration ttl = Duration.ofHours(48);
  private int length = 8;
  private String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

  public Duration getTtl() {
    return ttl;
  }

  public void setTtl(Duration ttl) {
    this.ttl = ttl;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public String getAlphabet() {
    return alphabet;
  }

  public void setAlphabet(String alphabet) {
    this.alphabet = alphabet;
  }
}
