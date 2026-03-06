// Copyright (c) UrbanGate
package com.urbangate.iam.configuration;

import static com.urbangate.iam.tenant.TenantResolutionFilter.resolveTenant;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final KeycloakProperties keycloakProperties;

  private static final String[] PUBLIC_ENDPOINTS = {
    // Auth
    "/api/v1/auth/login",
    "/api/v1/auth/register",
    "/api/v1/auth/refresh",
    "/api/v1/auth/forgot-password",
    "/api/v1/auth/reset-password",
    "/api/v1/auth/password-set-up",
    "/api/v1/auth/confirm",
    "/api/v1/version",
    // Swagger
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/swagger-ui.html",
    // Actuator
    "/actuator/health",
    "/actuator/info"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(PUBLIC_ENDPOINTS)
                    .permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()

                    // Admin-only endpoints
                    .requestMatchers("/api/v1/platform/**")
                    .hasRole("ADMIN")

                    // Manager endpoints
                    .requestMatchers("/api/v1/manager/**")
                    .hasAnyRole("ADMIN", "MANAGER")

                    // User endpoints require authentication
                    .requestMatchers("/api/v1/users/**")
                    .authenticated()
                    .requestMatchers("/api/v1/roles/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/v1/permissions/**")
                    .hasRole("ADMIN")

                    // Everything else requires authentication
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.authenticationManagerResolver(authenticationManagerResolver()));

    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
    return converter;
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    List<GrantedAuthority> authorities = new ArrayList<>();

    // Extract realm-level roles
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess != null && realmAccess.containsKey("roles")) {
      log.info("This is the roles claim for realm_access");
      List<String> realmRoles = (List<String>) realmAccess.get("roles");
      realmRoles.stream()
          .filter(
              role ->
                  !role.startsWith("default-roles")
                      && !role.equals("offline_access")
                      && !role.equals("uma_authorization"))
          .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
          .forEach(authorities::add);
    }

    // Extract client-level roles for fine-grained access
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    log.info("This is the roles claim for resource_access");
    if (resourceAccess != null) {
      resourceAccess.forEach(
          (clientId, clientData) -> {
            if (clientData instanceof Map) {
              Map<String, Object> clientMap = (Map<String, Object>) clientData;
              if (clientMap.containsKey("roles")) {
                List<String> clientRoles = (List<String>) clientMap.get("roles");
                clientRoles.stream()
                    .map(
                        role ->
                            new SimpleGrantedAuthority(
                                "ROLE_" + clientId.toUpperCase() + "_" + role.toUpperCase()))
                    .forEach(authorities::add);
              }
            }
          });
    }

    // Extract scopes as authorities (for fine-grained permissions)
    String scope = jwt.getClaim("scope");
    if (scope != null) {
      log.info("This is the roles claim for scope");
      for (String s : scope.split(" ")) {
        if (!s.isBlank()) {
          authorities.add(new SimpleGrantedAuthority("SCOPE_" + s));
        }
      }
    }

    return authorities;
  }

  private final Map<String, JwtDecoder> decoderCache = new ConcurrentHashMap<>();

  @Bean
  public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
    return request -> {
      String tenantId = resolveTenant(request);

      if (tenantId == null || tenantId.isBlank()) {
        throw new IllegalArgumentException("Missing tenant identifier");
      }

      String issuerUri =
          String.format("%s/realms/tenant_%s", keycloakProperties.getUrl(), tenantId);

      // Cache decoders — avoid hitting Keycloak OIDC discovery on every request
      JwtDecoder decoder =
          decoderCache.computeIfAbsent(
              tenantId, id -> JwtDecoders.fromOidcIssuerLocation(issuerUri));

      JwtAuthenticationProvider provider = new JwtAuthenticationProvider(decoder);
      provider.setJwtAuthenticationConverter(jwtAuthenticationConverter());

      return provider::authenticate;
    };
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setExposedHeaders(List.of("Authorization", "X-Refresh-Token"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
