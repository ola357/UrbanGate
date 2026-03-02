// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import com.urbangate.iam.configuration.KeycloakProperties;
import com.urbangate.iam.util.TenantContext;
import com.urbangate.shared.dto.SmtpConfig;
import com.urbangate.shared.dto.TenantAdditionalInfo;
import com.urbangate.shared.dto.TenantConfig;
import com.urbangate.shared.entity.TenantConfiguration;
import com.urbangate.shared.enums.ExceptionResponse;
import com.urbangate.shared.exceptions.ConflictException;
import com.urbangate.shared.repository.TenantConfigurationRepository;
import com.urbangate.shared.service.TenantConfigurationRedisImpl;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantDiscoveryKeyCloakService {

  private final Keycloak keycloak;
  private final TenantConfigurationRepository tenantConfigurationRepository;
  private final TenantConfigurationRedisImpl tenantConfigurationRedisImpl;

  private static final String REALM_PREFIX = "tenant_";
  private final KeycloakProperties keycloakProperties;

  /**
   * Returns the RealmResource for the current request's tenant. All user/role operations go through
   * this.
   */
  public RealmResource currentRealm() {
    String realmName = realmNameFor(TenantContext.getTenantId());
    return keycloak.realm(realmName);
  }

  public RealmResource realmFor(String tenantId) {
    return keycloak.realm(realmNameFor(tenantId));
  }

  /**
   * Provisions a brand new realm for a new tenant. Called when a company signs up for your
   * platform.
   */
  public void provisionTenant(String tenantId, TenantConfig config) {
    String realmName = realmNameFor(tenantId);
    log.info("Provisioning new tenant realm: {}", realmName);

    // Check if already exists
    boolean exists =
        keycloak.realms().findAll().stream().anyMatch(r -> r.getRealm().equals(realmName));
    if (exists) {
      throw new ConflictException(ExceptionResponse.TENANT_ALREADY_REGISTERED);
    }

    // Build realm
    RealmRepresentation realm = new RealmRepresentation();
    realm.setRealm(realmName);
    realm.setDisplayName(config.displayName());
    realm.setEnabled(true);
    realm.setRegistrationAllowed(false);
    realm.setLoginWithEmailAllowed(true);
    realm.setDuplicateEmailsAllowed(false);
    realm.setResetPasswordAllowed(true);
    realm.setBruteForceProtected(true);
    realm.setAccessTokenLifespan(300);
    realm.setSsoSessionIdleTimeout(1800);

    // Per-tenant password policy
    realm.setPasswordPolicy(
        "length(8) and upperCase(1) and lowerCase(1) and digits(1) and specialChars(1)");

    keycloak.realms().create(realm);
    log.info("Realm created: {}", realmName);

    // Set up default roles, clients, SMTP for this tenant

    setupDefaultRoles(realmName);
    setupMobileClient(realmName);
    createTenantConfiguration(tenantId, config.tenantAdditionalInfo());
    if (config.smtpConfig() != null) {
      setupSmtp(realmName, config.smtpConfig());
    }
  }

  private void createTenantConfiguration(String tenantId, TenantAdditionalInfo additionalInfo) {
    TenantConfiguration tenantConfiguration = new TenantConfiguration();
    tenantConfiguration.setRealm(realmNameFor(tenantId));
    tenantConfiguration.setName(additionalInfo.name());
    tenantConfiguration.setDescription(additionalInfo.description());
    tenantConfiguration.setIcon(additionalInfo.icon());
    tenantConfiguration.setCreator(additionalInfo.creator());
    tenantConfiguration.setEstateCode(additionalInfo.estateCode());
    tenantConfiguration.setAddress(additionalInfo.address());
    tenantConfiguration.setState(additionalInfo.state());
    tenantConfiguration.setPhone(additionalInfo.phone());
    tenantConfiguration.setMaximumGuestsForMultipleCode(
        additionalInfo.maximumGuestsForMultipleCode());
    tenantConfiguration.setNumberOfDaysBeforeOverdue(additionalInfo.numberOfDaysBeforeOverdue());
    tenantConfiguration.setNumberOfDaysBeforeUpcomingPayment(
        additionalInfo.numberOfDaysBeforeUpcomingPayment());
    tenantConfiguration.setSendBirthdayShout(additionalInfo.sendBirthdayShout());
    tenantConfiguration.setPayableBills(additionalInfo.payableBills());
    tenantConfiguration.setDefaultAccessCodeExpiryInMinutes(
        additionalInfo.defaultAccessCodeExpiryInMinutes());

    TenantConfiguration saved = tenantConfigurationRepository.insert(tenantConfiguration);
    tenantConfigurationRedisImpl.save(saved);
  }

  public void deleteTenant(String tenantId) {
    String realmName = realmNameFor(tenantId);
    keycloak.realm(realmName).remove();
    log.info("Tenant realm deleted: {}", realmName);
  }

  public String realmNameFor(String tenantId) {
    return REALM_PREFIX + tenantId;
  }

  private void setupDefaultRoles(String realmName) {
    RealmResource realm = keycloak.realm(realmName);
    for (String role : List.of("admin", "manager", "resident")) {
      var r = new org.keycloak.representations.idm.RoleRepresentation();
      r.setName(role);
      realm.roles().create(r);
    }
    log.info("Default roles created for realm: {}", realmName);
  }

  private void setupMobileClient(String realmName) {
    var client = new org.keycloak.representations.idm.ClientRepresentation();
    client.setClientId(keycloakProperties.getPublicClientId());
    client.setPublicClient(true);
    client.setDirectAccessGrantsEnabled(true);
    client.setEnabled(true);
    client.setRedirectUris(List.of(keycloakProperties.getRedirectUri()));
    keycloak.realm(realmName).clients().create(client);
    log.info("Mobile client created for realm: {}", realmName);
  }

  private void setupSmtp(String realmName, SmtpConfig smtp) {
    RealmRepresentation realm = keycloak.realm(realmName).toRepresentation();
    realm.setSmtpServer(
        Map.of(
            "host", smtp.host(),
            "port", smtp.port(),
            "from", smtp.from(),
            "auth", "true",
            "starttls", "true",
            "user", smtp.user(),
            "password", smtp.password()));
    keycloak.realm(realmName).update(realm);
  }
}
