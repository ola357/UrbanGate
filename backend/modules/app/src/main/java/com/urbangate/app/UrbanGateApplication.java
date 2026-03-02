// Copyright (c) UrbanGate
package com.urbangate.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.urbangate")
@EnableScheduling
public class UrbanGateApplication {

  public static void main(String[] args) {
    SpringApplication.run(UrbanGateApplication.class, args);
  }
}
