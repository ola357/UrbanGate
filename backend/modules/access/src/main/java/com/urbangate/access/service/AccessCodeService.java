// Copyright (c) UrbanGate
package com.urbangate.access.service;

import com.urbangate.access.dto.request.AccessCodeRequest;
import com.urbangate.access.dto.response.AccessCodeResponse;
import com.urbangate.access.entity.AccessCode;
import com.urbangate.access.entity.Visitor;
import com.urbangate.access.repository.AccessCodeRepository;
import com.urbangate.access.repository.VisitorRepository;
import com.urbangate.access.repository.impl.AccessCodeRedisImpl;
import com.urbangate.shared.entity.TenantConfiguration;
import com.urbangate.shared.repository.TenantConfigurationRepository;
import com.urbangate.shared.service.EmailService;
import com.urbangate.shared.service.TenantConfigurationRedisImpl;
import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessCodeService {

  private final AccessCodeRepository accessCodeRepository;
  private final EmailService emailService;
  private final AccessCodeRedisImpl accessCodeRedis;
  private final VisitorRepository visitorRepository;
  private final Keycloak keycloak;
  private final TenantConfigurationRedisImpl tenantConfigurationRedis;
  private final TenantConfigurationRepository tenantConfigurationRepository;
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  // TODOX : ADD CHECKIN FLAG TO ACCESS CODES

  public AccessCodeResponse createAccessCode(
      AccessCodeRequest accessCodeRequest, Jwt jwt, HttpServletRequest request) {

    try {

      String tenant = resolveTenant(request);
      String useId = jwt.getSubject();
      AccessCode accessCode = new AccessCode();
      accessCode.setAccessType(accessCodeRequest.accessType());
      accessCode.setActive(true);
      accessCode.setDescription(accessCodeRequest.accessType().getDescription());
      accessCode.setGroupName(accessCodeRequest.groupName());
      accessCode.setNoOfGuests(accessCodeRequest.noOfGuests());
      accessCode.setPurposeOfVisit(accessCodeRequest.purposeOfVisit());
      accessCode.setExpiryTime(accessCodeRequest.expiryDate());
      accessCode.setStartTime(accessCodeRequest.startDate());
      accessCode.setUserId(useId);
      accessCode.setRealm(tenant);

      String code = generateAccessCode();
      log.debug("Generated access code= {} for user= {}", code, useId);
      accessCode.setCode(code);

      // Create Visitor

      accessCodeRepository.insert(accessCode);

      Visitor visitor = new Visitor();
      visitor.setAccessCode(code);
      visitor.setName(accessCodeRequest.visitorName());
      visitor.setPhone(accessCodeRequest.visitorPhone());
      visitor.setEmail(accessCodeRequest.visitorEmail());
      visitor.setVisitorType(accessCodeRequest.visitorType());
      visitorRepository.insert(visitor);

      accessCodeRedis.save(accessCode);
      emailService.sendEmail(jwt.getClaimAsString("email"), code);
      return new AccessCodeResponse(
          code,
          accessCode.getExpiryTime(),
          accessCode.getAccessType(),
          accessCode.getStartTime(),
          accessCode.isActive(),
          accessCodeRequest.visitorName());
    } catch (Exception e) {
      throw new IllegalArgumentException("Error while creating access code", e);
    }
  }

  public List<AccessCodeResponse> getAccessCodesByUser(Jwt jwt) {
    String userId = jwt.getSubject();

    List<AccessCode> accessCodes = accessCodeRepository.findAllByUser(userId);

    log.info("Found {} access codes", accessCodes.size());
    return accessCodes.stream()
        .map(
            accessCode -> {
              Visitor visitor =
                  visitorRepository
                      .findByAccessCode(accessCode.getCode())
                      .orElseThrow(
                          () ->
                              new RuntimeException(
                                  "No visitor found for access code= " + accessCode.getCode()));
              log.info(
                  "Found visitor for access code= {} for user= {}", accessCode.getCode(), userId);
              return new AccessCodeResponse(
                  accessCode.getCode(),
                  accessCode.getExpiryTime(),
                  accessCode.getAccessType(),
                  accessCode.getStartTime(),
                  accessCode.isActive(),
                  visitor.getName());
            })
        .toList();
  }

  public String revokeAccessCode(String accessCode) {
    AccessCode code = getAccessCodeCheckRedisFirst(accessCode);
    boolean isRevoked = accessCodeRepository.revokeCode(code.getCode());
    if (isRevoked) {
      accessCodeRedis.evict(accessCode);
      AccessCode updatedAccessCode = getAccessCode(accessCode);
      accessCodeRedis.save(updatedAccessCode);
      return "Successfully revoked access code";
    }
    throw new IllegalArgumentException("Access code not revoked");
  }

  public AccessCode getAccessCodeCheckRedisFirst(String accessCode) {
    return accessCodeRedis
        .findById(accessCode)
        .orElseGet(
            () ->
                accessCodeRepository
                    .findByCode(accessCode)
                    .orElseThrow(() -> new IllegalArgumentException("Access code not found")));
  }

  public AccessCode getAccessCode(String accessCode) {
    return accessCodeRepository
        .findByCode(accessCode)
        .orElseThrow(() -> new IllegalArgumentException("Access code not found"));
  }

  public String extendExpirationTimeForAccessCode(String accessCode, String expiryTime) {
    Timestamp newTime = Timestamp.valueOf(expiryTime);
    accessCodeRedis.evict(accessCode);
    AccessCode code = getAccessCodeCheckRedisFirst(accessCode);
    if (code.isActive()) {
      boolean isUpdated = accessCodeRepository.updateExpiryTime(accessCode, newTime);
      if (isUpdated) {
        AccessCode updatedCode = getAccessCode(accessCode);
        accessCodeRedis.save(updatedCode);
        return "Successfully updated access code";
      }
      throw new IllegalArgumentException("Access code not updated");
    } else {
      throw new IllegalArgumentException("You Can Extend Expiry For Expired Access Code");
    }
  }

  @Scheduled(fixedDelayString = "${scheduler.access-code.revoke-schedule-time}")
  public void revokeExpiredAccessCode() {
    keycloak
        .realms()
        .findAll()
        .forEach(
            realmRepresentation -> {
              String realm = realmRepresentation.getRealm();
              TenantConfiguration tenantConfiguration =
                  tenantConfigurationRedis
                      .findById(realm)
                      .orElseGet(
                          () -> tenantConfigurationRepository.findByRealm(realm).orElse(null));
              List<AccessCode> accessCodes = new ArrayList<>();
              if (tenantConfiguration != null) {
                accessCodes = accessCodeRepository.findAllByRealm(tenantConfiguration.getRealm());
              } else {
                log.warn("No tenant configuration found for realm: {}", realm);
              }

              accessCodes.stream()
                  .filter(AccessCode::isActive)
                  .forEach(
                      accessCode -> {
                        if (Objects.isNull(accessCode.getExpiryTime())) {
                          LocalDateTime expiryTime =
                              LocalDateTime.now()
                                  .plusMinutes(
                                      tenantConfiguration.getDefaultAccessCodeExpiryInMinutes());

                          if (LocalDateTime.now().isAfter(expiryTime)) {
                            revokeAccessCodeInScheduler(realm, accessCode);
                          }

                        } else {
                          if (Timestamp.valueOf(LocalDateTime.now())
                              .after(accessCode.getExpiryTime())) {
                            revokeAccessCodeInScheduler(realm, accessCode);
                          }
                        }
                      });
            });
  }

  private void revokeAccessCodeInScheduler(String realm, AccessCode accessCode) {
    boolean isUpdated = accessCodeRepository.revokeCode(accessCode.getCode());
    if (isUpdated) {
      log.info("Successfully revoked access code = {} for realm = {}", accessCode.getCode(), realm);
    } else {
      log.warn("Unable to revoke access code = {} for realm = {}", accessCode.getCode(), realm);
    }
  }

  public static String generateAccessCode() {
    return IntStream.range(0, 6)
        .mapToObj(i -> String.valueOf(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()))))
        .collect(Collectors.joining());
  }

  public static String resolveTenant(HttpServletRequest request) {

    String fromHeader = request.getHeader("X-Tenant-ID");
    return fromHeader != null ? "tenant_" + fromHeader.toLowerCase() : null;
  }
}
