// Copyright (c) UrbanGate
package com.urbangate.app.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangate.iam.service.TenantDiscoveryKeyCloakService;
import com.urbangate.iam.util.TenantResolutionFilter;
import com.urbangate.shared.dto.SmtpConfig;
import com.urbangate.shared.dto.TenantAdditionalInfo;
import com.urbangate.shared.dto.TenantConfig;
import com.urbangate.shared.enums.PayableBills;
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

@WebMvcTest(
    value = TenantController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = TenantResolutionFilter.class))
@Import(TenantControllerTest.TestSecurityConfig.class)
@DisplayName("TenantController — /api/v1/platform/tenants")
class TenantControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockitoBean TenantDiscoveryKeyCloakService tenantService;

  private static final String BASE_URL = "/api/v1/platform/tenants";
  private static final String TENANT_HEADER = "avera";

  private TenantConfig validConfig;

  @BeforeEach
  void setUp() {
    validConfig =
        new TenantConfig(
            "urbangate",
            new SmtpConfig("localhost", "456", "urban@gamil.com", "vincent", "password"),
            new TenantAdditionalInfo(
                "urbangate",
                "esate in lagos",
                "icon.png",
                "vincent",
                "sangotedo",
                "lagos",
                "09055589964",
                9,
                5,
                "URBGAT",
                false,
                8,
                15,
                List.of(PayableBills.WASTE)));
  }

  @Nested
  @DisplayName("POST /api/v1/platform/tenants/{tenantId}")
  class ProvisionTenant {

    @Test
    @DisplayName("✅ returns 201 with confirmation message when tenant is provisioned")
    void provision_validTenant_returns201() throws Exception {
      willDoNothing().given(tenantService).provisionTenant(eq("acme"), any(TenantConfig.class));

      mockMvc
          .perform(
              post(BASE_URL + "/acme")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("X-Tenant-ID", TENANT_HEADER)
                  .content(objectMapper.writeValueAsString(validConfig)))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.message").value("success"))
          .andExpect(jsonPath("$.data").value("Tenant 'acme' provisioned"));
    }

    @Test
    @DisplayName("❌ returns 409 when tenant already exists")
    void provision_duplicateTenant_returns409() throws Exception {
      willThrow(new ConflictException("Tenant 'acme' already exists"))
          .given(tenantService)
          .provisionTenant(eq("acme"), any(TenantConfig.class));

      mockMvc
          .perform(
              post(BASE_URL + "/acme")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("X-Tenant-ID", TENANT_HEADER)
                  .content(objectMapper.writeValueAsString(validConfig)))
          .andDo(print())
          .andExpect(jsonPath("$.detail").value("Tenant 'acme' already exists"));
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/platform/tenants/{tenantId}")
  class DeleteTenant {

    @Test
    @DisplayName("✅ returns 200 with confirmation message when tenant is deleted")
    void delete_existingTenant_returns200() throws Exception {
      willDoNothing().given(tenantService).deleteTenant("acme");

      mockMvc
          .perform(delete(BASE_URL + "/acme").header("X-Tenant-ID", TENANT_HEADER))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("success"))
          .andExpect(jsonPath("$.data").value("Tenant 'acme' deleted"));
    }

    @Test
    @DisplayName("❌ returns 404 when tenant does not exist")
    void delete_nonExistentTenant_returns404() throws Exception {
      willThrow(new ResourceNotFoundException("Tenant 'ghost' not found"))
          .given(tenantService)
          .deleteTenant("ghost");

      mockMvc
          .perform(delete(BASE_URL + "/ghost").header("X-Tenant-ID", TENANT_HEADER))
          .andDo(print())
          .andExpect(status().isInternalServerError())
          .andExpect(jsonPath("$.detail").value("Tenant 'ghost' not found"));
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
