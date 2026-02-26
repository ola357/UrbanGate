// Copyright (c) UrbanGate
package com.urbangate.app.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangate.iam.dto.request.CreateRoleRequest;
import com.urbangate.iam.dto.response.RoleResponse;
import com.urbangate.iam.service.KeycloakRoleService;
import com.urbangate.iam.tenant.TenantResolutionFilter;
import com.urbangate.shared.exceptions.ConflictException;
import com.urbangate.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
    value = RoleController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = TenantResolutionFilter.class))
@Import(RoleControllerTest.TestSecurityConfig.class)
@DisplayName("RoleController — /api/v1/roles")
class RoleControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockitoBean KeycloakRoleService roleService;

  private static final String BASE_URL = "/api/v1/roles";
  private static final String TENANT = "avera";

  private RoleResponse adminRole;
  private RoleResponse customRole;

  @BeforeEach
  void setUp() {
    adminRole = new RoleResponse("1", "admin", "Administrator role with full access");
    customRole = new RoleResponse("2", "property_manager", "Manages property listings");
  }

  @Nested
  @DisplayName("GET /api/v1/roles")
  class ListRoles {

    @Test
    @DisplayName("✅ returns 200 with list of all roles")
    void listRoles_rolesExist_returns200WithList() throws Exception {
      given(roleService.getAllRoles()).willReturn(List.of(adminRole, customRole));

      mockMvc
          .perform(get(BASE_URL).header("X-Tenant-ID", TENANT))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("success"))
          .andExpect(jsonPath("$.data.length()").value(2))
          .andExpect(jsonPath("$.data[0].name").value("admin"))
          .andExpect(jsonPath("$.data[1].name").value("property_manager"));
    }

    @Test
    @DisplayName("❌ returns 500 when Keycloak is unavailable")
    void listRoles_keycloakUnavailable_returns500() throws Exception {
      given(roleService.getAllRoles())
          .willThrow(new RuntimeException("Keycloak connection refused"));

      mockMvc
          .perform(get(BASE_URL).header("X-Tenant-ID", TENANT))
          .andDo(print())
          .andExpect(status().isInternalServerError());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/roles/{name}")
  class GetRole {

    @Test
    @DisplayName("✅ returns 200 with role details for existing role")
    void getRole_roleExists_returns200WithRole() throws Exception {
      given(roleService.getRoleByName("admin")).willReturn(adminRole);

      mockMvc
          .perform(get(BASE_URL + "/admin").header("X-Tenant-ID", TENANT))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("success"))
          .andExpect(jsonPath("$.data.name").value("admin"))
          .andExpect(jsonPath("$.data.description").value("Administrator role with full access"));
    }

    @Test
    @DisplayName("❌ returns 404 when role does not exist")
    void getRole_roleNotFound_returns404() throws Exception {
      given(roleService.getRoleByName("nonexistent_role"))
          .willThrow(new ResourceNotFoundException("Role 'nonexistent_role' not found"));

      mockMvc
          .perform(get(BASE_URL + "/nonexistent_role").header("X-Tenant-ID", TENANT))
          .andDo(print())
          .andExpect(status().isInternalServerError())
          .andExpect(jsonPath("$.detail").value("Role 'nonexistent_role' not found"));
    }
  }

  @Nested
  @DisplayName("POST /api/v1/roles")
  class CreateRole {

    @Test
    @DisplayName("✅ returns 201 with created role on valid request")
    void createRole_validRequest_returns201WithRole() throws Exception {
      CreateRoleRequest request =
          new CreateRoleRequest("property_manager", "Manages property listings");

      given(roleService.createRole(any(CreateRoleRequest.class))).willReturn(customRole);

      performPost(request)
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.message").value("success"))
          .andExpect(jsonPath("$.data.name").value("property_manager"))
          .andExpect(jsonPath("$.data.description").value("Manages property listings"));
    }

    @Test
    @DisplayName("❌ returns 409 when role name already exists")
    void createRole_duplicateName_returns409() throws Exception {
      CreateRoleRequest request = new CreateRoleRequest("admin", "Another admin role");

      given(roleService.createRole(any(CreateRoleRequest.class)))
          .willThrow(new ConflictException("Role 'admin' already exists"));

      performPost(request)
          .andDo(print())
          .andExpect(jsonPath("$.detail").value("Role 'admin' already exists"));
    }

    private ResultActions performPost(Object body) throws Exception {
      return mockMvc.perform(
          post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .header("X-Tenant-ID", TENANT)
              .content(objectMapper.writeValueAsString(body)));
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/roles/{name}")
  class DeleteRole {

    @Test
    @DisplayName("✅ returns 200 with confirmation message on successful deletion")
    void deleteRole_customRole_returns200() throws Exception {
      doNothing().when(roleService).deleteRole("property_manager");

      mockMvc
          .perform(delete(BASE_URL + "/property_manager").header("X-Tenant-ID", TENANT))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("success"))
          .andExpect(jsonPath("$.data").value("Role deleted"));
    }

    @Test
    @DisplayName("❌ returns 400 when attempting to delete a protected role")
    void deleteRole_protectedRole_returns400() throws Exception {
      willThrow(new IllegalArgumentException("Role 'admin' is protected and cannot be deleted"))
          .given(roleService)
          .deleteRole("admin");

      mockMvc
          .perform(delete(BASE_URL + "/admin").header("X-Tenant-ID", TENANT))
          .andDo(print())
          .andExpect(jsonPath("$.detail").value("Role 'admin' is protected and cannot be deleted"));
    }
  }

  @TestConfiguration
  @EnableWebSecurity
  static class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
      return http.build();
    }
  }
}
