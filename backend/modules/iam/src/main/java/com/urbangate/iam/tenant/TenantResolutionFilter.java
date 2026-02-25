// Copyright (c) UrbanGate
package com.urbangate.iam.tenant;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class TenantResolutionFilter implements Filter {

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    String path = request.getRequestURI();

    log.debug("Request URI: {}", path);

    if (isExcluded(path)) {
      chain.doFilter(req, res);
      return;
    }
    try {
      String tenantId = resolveTenant(request);

      if (tenantId == null || tenantId.isBlank()) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"success\":false,\"message\":\"Missing tenant identifier\"}");
        return;
      }

      // Sanitize — only allow alphanumeric + hyphens
      if (!tenantId.matches("^[a-z0-9-]+$")) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"success\":false,\"message\":\"Invalid tenant identifier\"}");
        return;
      }

      log.debug("Resolved tenant: {}", tenantId);
      TenantContext.setTenantId(tenantId);
      chain.doFilter(req, res);

    } finally {
      TenantContext.clear();
    }
  }

  public static String resolveTenant(HttpServletRequest request) {

    String fromHeader = request.getHeader("X-Tenant-ID");
    return fromHeader != null ? fromHeader.toLowerCase() : null;
  }

  private boolean isExcluded(String path) {
    return path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/swagger-resources")
        || path.startsWith("/webjars")
        || path.startsWith("/api/v1/platform/tenants")
        || path.startsWith("/actuator");
  }
}
