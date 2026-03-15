// Copyright (c) UrbanGate
package com.urbangate.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangate.shared.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantContextFilter extends OncePerRequestFilter {
  private static final List<String> PUBLIC_PATHS =
      List.of("/api/v1/version", "/actuator/health", "/actuator/health/**");

  private final UrbangateSecurityProperties properties;
  private final ObjectMapper objectMapper;
  private final PathMatcher pathMatcher = new AntPathMatcher();

  public TenantContextFilter(UrbangateSecurityProperties properties, ObjectMapper objectMapper) {
    this.properties = properties;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    TenantContext.clear();
    if (isPublicPath(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String tenantId = resolveTenantFromJwt();
    if ((tenantId == null || tenantId.isBlank()) && properties.isAllowHeaderTenant()) {
      tenantId = request.getHeader(properties.getTenantHeader());
    }

    if (tenantId == null || tenantId.isBlank()) {
      if (properties.isEnabled()) {
        writeMissingTenant(response, request.getRequestURI());
        return;
      }
      filterChain.doFilter(request, response);
      return;
    }

    TenantContext.setTenant(tenantId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      TenantContext.clear();
    }
  }

  private boolean isPublicPath(HttpServletRequest request) {
    String path = request.getRequestURI();
    for (String pattern : PUBLIC_PATHS) {
      if (pathMatcher.match(pattern, path)) {
        return true;
      }
    }
    return false;
  }

  private String resolveTenantFromJwt() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtToken) {
      Object claim = jwtToken.getToken().getClaim(properties.getTenantClaim());
      if (claim != null) {
        return String.valueOf(claim);
      }
    }
    return null;
  }

  private void writeMissingTenant(HttpServletResponse response, String path) throws IOException {
    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle("Tenant required");
    problemDetail.setDetail("Tenant id is required for this request.");
    problemDetail.setProperty("timestamp", Instant.now().toString());
    problemDetail.setProperty("path", path);

    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    objectMapper.writeValue(response.getOutputStream(), problemDetail);
  }

  @Override
  protected boolean shouldNotFilterErrorDispatch() {
    return false;
  }
}
