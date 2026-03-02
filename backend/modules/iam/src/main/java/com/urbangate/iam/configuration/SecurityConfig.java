// Copyright (c) UrbanGate
package com.urbangate.iam.configuration;

import static com.urbangate.iam.tenant.TenantResolutionFilter.resolveTenant;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
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

  private static final String ADMIN_ROLE = "ADMIN";
  private static final String ROLE_LITERAL = "roles";
  private static final String ROLE_PREFIX = "ROLE_";

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
                    .hasRole(ADMIN_ROLE)

                    // Manager endpoints
                    .requestMatchers("/api/v1/manager/**")
                    .hasAnyRole(ADMIN_ROLE, "MANAGER")

                    // User endpoints require authentication
                    .requestMatchers("/api/v1/users/**")
                    .authenticated()
                    .requestMatchers("/api/v1/roles/**")
                    .hasRole(ADMIN_ROLE)
                    .requestMatchers("/api/v1/permissions/**")
                    .hasRole(ADMIN_ROLE)

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
    authorities.addAll(extractRealmRoles(jwt));
    authorities.addAll(extractClientRoles(jwt));
    authorities.addAll(extractScopes(jwt));
    return authorities;
  }

  private List<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess == null || !realmAccess.containsKey(ROLE_LITERAL)) {
      return List.of();
    }

    log.info("Extracting realm_access roles");
    List<String> realmRoles = (List<String>) realmAccess.get(ROLE_LITERAL);

    return realmRoles.stream()
        .filter(
            role ->
                !role.startsWith("default-roles")
                    && !role.equals("offline_access")
                    && !role.equals("uma_authorization"))
        .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
        .toList();
  }

  private List<? extends GrantedAuthority> extractClientRoles(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    if (resourceAccess == null) {
      return List.of();
    }

    log.info("Extracting resource_access roles");
    return resourceAccess.entrySet().stream()
        .filter(entry -> entry.getValue() instanceof Map)
        .flatMap(
            entry ->
                extractRolesFromClient(entry.getKey(), (Map<String, Object>) entry.getValue())
                    .stream())
        .toList();
  }

  private List<? extends GrantedAuthority> extractRolesFromClient(
      String clientId, Map<String, Object> clientMap) {
    if (!clientMap.containsKey(ROLE_LITERAL)) {
      return List.of();
    }

    List<String> clientRoles = (List<String>) clientMap.get(ROLE_LITERAL);
    return clientRoles.stream()
        .map(
            role ->
                new SimpleGrantedAuthority(
                    ROLE_PREFIX + clientId.toUpperCase() + "_" + role.toUpperCase()))
        .toList();
  }

  private List<? extends GrantedAuthority> extractScopes(Jwt jwt) {
    String scope = jwt.getClaim("scope");
    if (scope == null) {
      return List.of();
    }

    log.info("Extracting scopes");
    return Arrays.stream(scope.split(" "))
        .filter(s -> !s.isBlank())
        .map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
        .toList();
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
