// Copyright (c) UrbanGate
package com.urbangate.iam.notification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangate.iam.outbox.OutboxEvent;
import com.urbangate.iam.outbox.OutboxEventRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxEmailDispatcher {
  private static final String EMAIL_EVENT_PREFIX = "email.";

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;
  private final EmailSender emailSender;

  public OutboxEmailDispatcher(
      OutboxEventRepository outboxEventRepository,
      ObjectMapper objectMapper,
      EmailSender emailSender) {
    this.outboxEventRepository = outboxEventRepository;
    this.objectMapper = objectMapper;
    this.emailSender = emailSender;
  }

  @Transactional
  public int dispatchPendingEmails() {
    List<OutboxEvent> events =
        outboxEventRepository.findTop50ByPublishedAtIsNullOrderByOccurredAtAsc();
    int sent = 0;

    for (OutboxEvent event : events) {
      if (!event.getEventType().startsWith(EMAIL_EVENT_PREFIX)) {
        continue;
      }
      EmailMessage message = toEmailMessage(event);
      emailSender.send(message);
      event.setPublishedAt(Instant.now());
      sent++;
    }

    return sent;
  }

  private EmailMessage toEmailMessage(OutboxEvent event) {
    Map<String, Object> payload = parsePayload(event.getPayload());

    String to = String.valueOf(payload.getOrDefault("to", ""));
    String subject = "Welcome to UrbanGate";
    String body = "Your account is active. Welcome to UrbanGate.";
    return new EmailMessage(to, subject, body);
  }

  private Map<String, Object> parsePayload(String payload) {
    try {
      return objectMapper.readValue(payload, new TypeReference<>() {});
    } catch (Exception e) {
      throw new IllegalStateException("Failed to parse outbox payload", e);
    }
  }
}
