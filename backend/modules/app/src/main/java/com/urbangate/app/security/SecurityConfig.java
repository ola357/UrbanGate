// Copyright (c) UrbanGate
package com.urbangate.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(UrbangateSecurityProperties.class)
public class SecurityConfig {

  @Bean
  public TenantContextFilter tenantContextFilter(
      UrbangateSecurityProperties properties, ObjectMapper objectMapper) {
    return new TenantContextFilter(properties, objectMapper);
  }

  @Bean
  @ConditionalOnProperty(prefix = "urbangate.security", name = "enabled", havingValue = "true")
  public SecurityFilterChain securedSecurityFilterChain(
      HttpSecurity http, TenantContextFilter tenantContextFilter, UrbangateSecurityProperties props)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/v1/version", "/actuator/health/**")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/onboarding/activate",
                        "/api/v1/onboarding/password-resets",
                        "/api/v1/onboarding/password-resets/confirm")
                    .permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(
                    jwtConfig -> jwtConfig.jwtAuthenticationConverter(jwt -> toAuth(jwt, props))));

    http.addFilterAfter(tenantContextFilter, SecurityContextHolderFilter.class);
    return http.build();
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "urbangate.security",
      name = "enabled",
      havingValue = "false",
      matchIfMissing = true)
  public SecurityFilterChain openSecurityFilterChain(
      HttpSecurity http, TenantContextFilter tenantContextFilter) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

    http.addFilterAfter(tenantContextFilter, SecurityContextHolderFilter.class);
    return http.build();
  }

  private JwtAuthenticationToken toAuth(Jwt jwt, UrbangateSecurityProperties props) {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
    Collection<GrantedAuthority> scopeAuthorities = scopeConverter.convert(jwt);
    if (scopeAuthorities != null) {
      authorities.addAll(scopeAuthorities);
    }
    authorities.addAll(extractRoles(jwt, props));
    return new JwtAuthenticationToken(jwt, authorities);
  }

  private Collection<GrantedAuthority> extractRoles(Jwt jwt, UrbangateSecurityProperties props) {
    List<String> roles = new ArrayList<>();

    String rolesClaim = props.getRolesClaim();
    if (rolesClaim != null && !rolesClaim.isBlank()) {
      Object claim = jwt.getClaim(rolesClaim.split("\\.")[0]);
      if (claim instanceof Map<?, ?> map && rolesClaim.contains(".")) {
        String child = rolesClaim.substring(rolesClaim.indexOf('.') + 1);
        Object childValue = map.get(child);
        addRolesFromClaim(childValue, roles);
      } else {
        addRolesFromClaim(claim, roles);
      }
    }

    if (props.getResourceClientId() != null && !props.getResourceClientId().isBlank()) {
      Object resourceAccess = jwt.getClaim("resource_access");
      if (resourceAccess instanceof Map<?, ?> resourceMap) {
        Object clientAccess = resourceMap.get(props.getResourceClientId());
        if (clientAccess instanceof Map<?, ?> clientMap) {
          addRolesFromClaim(clientMap.get("roles"), roles);
        }
      }
    }

    return roles.stream()
        .filter(Objects::nonNull)
        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
        .distinct()
        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
        .toList();
  }

  @SuppressWarnings("unchecked")
  private void addRolesFromClaim(Object claim, List<String> target) {
    if (claim instanceof List<?> list) {
      for (Object item : list) {
        if (item != null) {
          target.add(String.valueOf(item));
        }
      }
    } else if (claim instanceof String single) {
      target.add(single);
    }
  }
}
