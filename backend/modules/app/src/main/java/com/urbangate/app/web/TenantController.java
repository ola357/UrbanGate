// Copyright (c) UrbanGate
package com.urbangate.app.web;

import com.urbangate.iam.tenant.TenantDiscoveryKeyCloakService;
import com.urbangate.shared.dto.ApiResponse;
import com.urbangate.shared.dto.TenantConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platform/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TenantController {

  private final TenantDiscoveryKeyCloakService tenantService;

  @PostMapping("/{tenantId}")
  public ResponseEntity<ApiResponse<?>> provision(
      @PathVariable String tenantId, @Valid @RequestBody TenantConfig config) {
    tenantService.provisionTenant(tenantId, config);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse<>().success("Tenant '" + tenantId + "' provisioned"));
  }

  @DeleteMapping("/{tenantId}")
  public ResponseEntity<ApiResponse<?>> delete(@PathVariable String tenantId) {
    tenantService.deleteTenant(tenantId);
    return ResponseEntity.ok(new ApiResponse<>().success("Tenant '" + tenantId + "' deleted"));
  }
}
