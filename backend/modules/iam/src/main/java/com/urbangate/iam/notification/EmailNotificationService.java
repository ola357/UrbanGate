// Copyright (c) UrbanGate
package com.urbangate.iam.notification;

import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.outbox.OutboxPublisher;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
  private final OutboxPublisher outboxPublisher;

  public EmailNotificationService(OutboxPublisher outboxPublisher) {
    this.outboxPublisher = outboxPublisher;
  }

  public void queueResidentOnboardedEmail(Resident resident) {
    if (resident.getEmail() == null || resident.getEmail().isBlank()) {
      return;
    }

    Map<String, Object> payload = new HashMap<>();
    payload.put("to", resident.getEmail());
    payload.put("residentId", resident.getId().toString());
    payload.put("estateId", resident.getEstate().getId().toString());
    payload.put("firstName", resident.getFirstName());
    payload.put("lastName", resident.getLastName());
    payload.put("type", "RESIDENT_ONBOARDED");

    outboxPublisher.publish(
        "Resident", resident.getId().toString(), "email.resident_onboarded", payload);
  }
}
