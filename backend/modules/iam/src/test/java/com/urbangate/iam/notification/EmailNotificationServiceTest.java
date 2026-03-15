// Copyright (c) UrbanGate
package com.urbangate.iam.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.urbangate.iam.domain.entity.Estate;
import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.outbox.OutboxPublisher;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {

  @Mock private OutboxPublisher outboxPublisher;

  private EmailNotificationService service;

  @BeforeEach
  void setUp() {
    service = new EmailNotificationService(outboxPublisher);
  }

  @Test
  void skipsWhenEmailIsMissing() {
    Resident resident = new Resident();
    resident.setEmail(" ");

    service.queueResidentOnboardedEmail(resident);

    verify(outboxPublisher, never()).publish(any(), any(), any(), any());
  }

  @Test
  void publishesOnboardedEmailEvent() {
    UUID estateId = UUID.randomUUID();
    UUID residentId = UUID.randomUUID();

    Estate estate = new Estate();
    estate.setId(estateId);

    Resident resident = new Resident();
    resident.setId(residentId);
    resident.setEstate(estate);
    resident.setEmail("user@example.com");
    resident.setFirstName("Jane");
    resident.setLastName("Doe");

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);

    service.queueResidentOnboardedEmail(resident);

    verify(outboxPublisher)
        .publish(
            eq("Resident"),
            eq(residentId.toString()),
            eq("email.resident_onboarded"),
            payloadCaptor.capture());

    Map<String, Object> payload = payloadCaptor.getValue();
    assertEquals("user@example.com", payload.get("to"));
    assertEquals(residentId.toString(), payload.get("residentId"));
    assertEquals(estateId.toString(), payload.get("estateId"));
    assertEquals("Jane", payload.get("firstName"));
    assertEquals("Doe", payload.get("lastName"));
    assertEquals("RESIDENT_ONBOARDED", payload.get("type"));
  }
}
