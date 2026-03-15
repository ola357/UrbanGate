// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.urbangate.iam.domain.entity.TenantEntity;
import com.urbangate.iam.domain.entity.TenantMembershipEntity;
import com.urbangate.iam.domain.entity.UserProfileEntity;
import com.urbangate.iam.dto.response.MeResponse;
import com.urbangate.iam.dto.response.MyTenantsResponse;
import com.urbangate.iam.repository.TenantMembershipRepository;
import com.urbangate.iam.repository.TenantRepository;
import com.urbangate.iam.repository.UserProfileRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class MeServiceTest {

  @Mock private UserProfileService userProfileService;
  @Mock private UserProfileRepository userProfileRepository;
  @Mock private TenantMembershipRepository membershipRepository;
  @Mock private TenantRepository tenantRepository;

  private MeService service;

  @BeforeEach
  void setUp() {
    service =
        new MeService(
            userProfileService, userProfileRepository, membershipRepository, tenantRepository);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void meReturnsCurrentUser() {
    Jwt jwt =
        new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            Map.of("sub", "user-1", "email", "user@example.com", "name", "User One"));
    SecurityContextHolder.getContext().setAuthentication(jwtAuth(jwt));

    MeResponse response = service.me();

    assertEquals("user-1", response.subject());
    assertEquals("user@example.com", response.email());
    assertEquals("User One", response.displayName());
    verify(userProfileService).upsert(eq("user-1"), eq("user@example.com"), eq("User One"));
  }

  @Test
  void myTenantsReturnsMemberships() {
    Jwt jwt =
        new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            Map.of("sub", "user-2", "email", "user2@example.com"));
    SecurityContextHolder.getContext().setAuthentication(jwtAuth(jwt));

    UserProfileEntity profile = new UserProfileEntity();
    profile.setId(UUID.randomUUID());
    profile.setSubject("user-2");
    when(userProfileRepository.findBySubject("user-2")).thenReturn(Optional.of(profile));

    TenantMembershipEntity membership = new TenantMembershipEntity();
    membership.setTenantId(UUID.randomUUID());
    membership.setUserId(profile.getId());
    membership.setRoles("ADMIN, USER");
    when(membershipRepository.findAllByUserId(profile.getId())).thenReturn(List.of(membership));

    TenantEntity tenant = new TenantEntity();
    tenant.setId(membership.getTenantId());
    tenant.setSlug("estate-1");
    tenant.setName("Estate One");
    when(tenantRepository.findById(membership.getTenantId())).thenReturn(Optional.of(tenant));

    MyTenantsResponse response = service.myTenants();

    assertEquals(1, response.tenants().size());
    var item = response.tenants().get(0);
    assertEquals(tenant.getId(), item.tenantId());
    assertEquals("estate-1", item.tenantSlug());
    assertEquals("Estate One", item.tenantName());
    assertEquals(Set.of("ADMIN", "USER"), item.roles());
  }

  private Authentication jwtAuth(Jwt jwt) {
    AbstractAuthenticationToken auth =
        new AbstractAuthenticationToken(List.of()) {
          @Override
          public Object getCredentials() {
            return "";
          }

          @Override
          public Object getPrincipal() {
            return jwt;
          }
        };
    auth.setAuthenticated(true);
    return auth;
  }
}
