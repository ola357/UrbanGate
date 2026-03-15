// Copyright (c) UrbanGate
package com.urbangate.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AutoConfigurationPackage(basePackages = "com.urbangate")
@SpringBootApplication(scanBasePackages = "com.urbangate")
public class UrbanGateApplication {

  public static void main(String[] args) {
    SpringApplication.run(UrbanGateApplication.class, args);
  }
}
