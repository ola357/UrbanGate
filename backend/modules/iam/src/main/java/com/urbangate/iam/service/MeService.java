// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import com.urbangate.iam.domain.entity.UserProfileEntity;
import com.urbangate.iam.dto.response.MeResponse;
import com.urbangate.iam.dto.response.MyTenantItem;
import com.urbangate.iam.dto.response.MyTenantsResponse;
import com.urbangate.iam.repository.TenantMembershipRepository;
import com.urbangate.iam.repository.TenantRepository;
import com.urbangate.iam.repository.UserProfileRepository;
import com.urbangate.shared.security.CurrentUser;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeService {

  private final UserProfileService userProfileService;
  private final UserProfileRepository userProfileRepository;
  private final TenantMembershipRepository membershipRepository;
  private final TenantRepository tenantRepository;

  @Transactional
  public MeResponse me() {
    String subject =
        CurrentUser.subject()
            .orElseThrow(() -> new IllegalStateException("Authenticated subject required"));
    String email =
        CurrentUser.email()
            .orElseThrow(() -> new IllegalStateException("Authenticated email required"));
    String displayName = CurrentUser.displayName().orElse(email);

    userProfileService.upsert(subject, email, displayName);

    return new MeResponse(subject, email, displayName);
  }

  @Transactional(readOnly = true)
  public MyTenantsResponse myTenants() {
    String subject =
        CurrentUser.subject()
            .orElseThrow(() -> new IllegalStateException("Authenticated subject required"));
    UserProfileEntity user =
        userProfileRepository
            .findBySubject(subject)
            .orElseThrow(() -> new IllegalStateException("User profile not found"));

    var items =
        membershipRepository.findAllByUserId(user.getId()).stream()
            .map(
                membership -> {
                  var tenant =
                      tenantRepository
                          .findById(membership.getTenantId())
                          .orElseThrow(
                              () ->
                                  new IllegalStateException(
                                      "Tenant not found: " + membership.getTenantId()));

                  Set<String> roles =
                      Arrays.stream(membership.getRoles().split(","))
                          .map(String::trim)
                          .filter(s -> !s.isBlank())
                          .collect(Collectors.toSet());

                  return new MyTenantItem(
                      tenant.getId(), tenant.getSlug(), tenant.getName(), roles);
                })
            .toList();

    return new MyTenantsResponse(items);
  }
}
