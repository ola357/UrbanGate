// Copyright (c) UrbanGate
package com.urbangate.app.web;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

  @GetMapping("/api/v1/version")
  public Map<String, Object> version() {
    return Map.of(
        "service", "urbangate-backend",
        "version", "0.0.1-SNAPSHOT",
        "time", Instant.now().toString());
  }
}
