// Copyright (c) UrbanGate
package com.urbangate.app.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private static final String CLIENT_ID = "ug-api";

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    List<GrantedAuthority> authorities = new ArrayList<>();

    // Realm roles
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
      roles.stream()
          .map(String::valueOf)
          .map(role -> "ROLE_" + role)
          .map(SimpleGrantedAuthority::new)
          .forEach(authorities::add);
    }

    // Client roles: resource_access.ug-api.roles
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    if (resourceAccess != null && resourceAccess.get(CLIENT_ID) instanceof Map<?, ?> client) {
      Object rolesObj = client.get("roles");
      if (rolesObj instanceof Collection<?> roles) {
        roles.stream()
            .map(String::valueOf)
            .map(role -> "ROLE_" + role)
            .map(SimpleGrantedAuthority::new)
            .forEach(authorities::add);
      }
    }

    return authorities;
  }
}
