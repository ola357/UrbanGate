// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import com.urbangate.iam.dto.request.CreateRoleRequest;
import com.urbangate.iam.dto.response.RoleResponse;
import com.urbangate.iam.util.TenantContext;
import com.urbangate.shared.enums.ExceptionResponse;
import com.urbangate.shared.exceptions.ConflictException;
import com.urbangate.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakRoleService {

  private final Keycloak keycloak;

  public RoleResponse createRole(CreateRoleRequest request) {
    log.info("Creating realm role: {}", request.name());

    try {
      getRealmResource().roles().get(request.name()).toRepresentation();
      throw new ConflictException("Role '" + request.name() + "' already exists");
    } catch (jakarta.ws.rs.NotFoundException ignored) {
      log.info("Role '{}' does not exist, going on to create a new one", request.name());
    }

    RoleRepresentation role = new RoleRepresentation();
    role.setName(request.name());
    role.setDescription(request.description());
    role.setClientRole(false);

    getRealmResource().roles().create(role);
    log.info("Role '{}' created", request.name());

    return getRoleByName(request.name());
  }

  public List<RoleResponse> getAllRoles() {
    return getRealmResource().roles().list().stream()
        .filter(
            r ->
                !r.getName().startsWith("default-roles")
                    && !r.getName().equals("offline_access")
                    && !r.getName().equals("uma_authorization"))
        .map(this::toRoleResponse)
        .toList();
  }

  public RoleResponse getRoleByName(String name) {
    try {
      RoleRepresentation role = getRealmResource().roles().get(name).toRepresentation();
      return toRoleResponse(role);
    } catch (jakarta.ws.rs.NotFoundException e) {
      throw new ResourceNotFoundException(ExceptionResponse.ROLE_NOT_FOUND);
    }
  }

  public void deleteRole(String name) {
    List<String> protectedRoles = List.of("admin", "user", "manager");
    if (protectedRoles.contains(name.toLowerCase())) {
      throw new IllegalArgumentException("Cannot delete protected role: " + name);
    }
    getRealmResource().roles().get(name).remove();
    log.info("Role '{}' deleted", name);
  }

  public void addCompositesToRole(String parentRoleName, List<String> childRoleNames) {
    List<RoleRepresentation> children =
        childRoleNames.stream()
            .map(
                name -> {
                  try {
                    return getRealmResource().roles().get(name).toRepresentation();
                  } catch (jakarta.ws.rs.NotFoundException e) {
                    throw new ResourceNotFoundException("Child role not found: " + name);
                  }
                })
            .toList();

    getRealmResource().roles().get(parentRoleName).addComposites(children);
    log.info("Added composites {} to role '{}'", childRoleNames, parentRoleName);
  }

  public List<RoleResponse> getRoleComposites(String roleName) {
    return getRealmResource().roles().get(roleName).getRoleComposites().stream()
        .map(this::toRoleResponse)
        .toList();
  }

  private RealmResource getRealmResource() {
    String realm = "tenant_" + TenantContext.getTenantId();
    return keycloak.realm(realm);
  }

  private RoleResponse toRoleResponse(RoleRepresentation role) {
    return new RoleResponse(role.getId(), role.getName(), role.getDescription());
  }
}
