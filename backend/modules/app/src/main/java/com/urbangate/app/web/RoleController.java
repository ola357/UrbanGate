// Copyright (c) UrbanGate
package com.urbangate.app.web;

import com.urbangate.iam.dto.request.CreateRoleRequest;
import com.urbangate.iam.dto.response.RoleResponse;
import com.urbangate.iam.service.KeycloakRoleService;
import com.urbangate.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Keycloak")
@Tag(
    name = "Role Management",
    description = "Manage realm roles and composite permissions. Requires ADMIN role.")
public class RoleController {

  private final KeycloakRoleService roleService;

  @GetMapping
  @Operation(summary = "List all realm roles")
  public ResponseEntity<ApiResponse<List<RoleResponse>>> listRoles() {
    return ResponseEntity.ok(
        new ApiResponse<List<RoleResponse>>().success(roleService.getAllRoles()));
  }

  @GetMapping("/{name}")
  @Operation(summary = "Get role by name")
  public ResponseEntity<ApiResponse<RoleResponse>> getRole(@PathVariable String name) {
    return ResponseEntity.ok(
        new ApiResponse<RoleResponse>().success(roleService.getRoleByName(name)));
  }

  @PostMapping
  @Operation(
      summary = "Create a new realm role",
      description =
          "Creates a custom role that can be assigned to users. Name must be lowercase with underscores/hyphens.")
  public ResponseEntity<ApiResponse<RoleResponse>> createRole(
      @Valid @RequestBody CreateRoleRequest request) {
    RoleResponse role = roleService.createRole(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse<RoleResponse>().success(role));
  }

  @DeleteMapping("/{name}")
  @Operation(
      summary = "Delete a realm role",
      description = "Protected roles (admin, user, manager) cannot be deleted.")
  public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable String name) {
    roleService.deleteRole(name);
    return ResponseEntity.ok(new ApiResponse<String>().success("Role deleted"));
  }
}
