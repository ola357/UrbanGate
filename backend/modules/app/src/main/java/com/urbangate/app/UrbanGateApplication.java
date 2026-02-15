package com.urbangate.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.urbangate")
public class UrbanGateApplication {

  public static void main(String[] args) {
    SpringApplication.run(UrbanGateApplication.class, args);
  }
}
