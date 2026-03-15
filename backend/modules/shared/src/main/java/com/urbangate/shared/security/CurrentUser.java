// Copyright (c) UrbanGate
package com.urbangate.shared.security;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class CurrentUser {

  private CurrentUser() {}

  public static Optional<Jwt> jwt() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      return Optional.empty();
    }
    if (auth.getPrincipal() instanceof Jwt jwt) {
      return Optional.of(jwt);
    }
    return Optional.empty();
  }

  public static Optional<String> subject() {
    return jwt().map(Jwt::getSubject);
  }

  public static Optional<String> email() {
    return jwt()
        .map(
            j -> {
              String email = j.getClaimAsString("email");
              return email != null ? email : j.getClaimAsString("preferred_username");
            });
  }

  public static Optional<String> displayName() {
    return jwt()
        .map(
            j -> {
              String name = j.getClaimAsString("name");
              if (name != null) {
                return name;
              }
              String given = j.getClaimAsString("given_name");
              String family = j.getClaimAsString("family_name");
              if (given != null && family != null) {
                return given + " " + family;
              }
              return j.getClaimAsString("preferred_username");
            });
  }
}
