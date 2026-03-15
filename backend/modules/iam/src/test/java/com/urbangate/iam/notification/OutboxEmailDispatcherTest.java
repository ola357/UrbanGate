// Copyright (c) UrbanGate
package com.urbangate.iam.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangate.iam.outbox.OutboxEvent;
import com.urbangate.iam.outbox.OutboxEventRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxEmailDispatcherTest {

  @Mock private OutboxEventRepository outboxEventRepository;
  @Mock private EmailSender emailSender;

  private OutboxEmailDispatcher dispatcher;

  @BeforeEach
  void setUp() {
    dispatcher = new OutboxEmailDispatcher(outboxEventRepository, new ObjectMapper(), emailSender);
  }

  @Test
  void dispatchesOnlyEmailEvents() throws Exception {
    OutboxEvent emailEvent = new OutboxEvent();
    emailEvent.setId(UUID.randomUUID());
    emailEvent.setEventType("email.resident_onboarded");
    emailEvent.setPayload(new ObjectMapper().writeValueAsString(Map.of("to", "user@example.com")));
    emailEvent.setOccurredAt(Instant.now());

    OutboxEvent nonEmailEvent = new OutboxEvent();
    nonEmailEvent.setId(UUID.randomUUID());
    nonEmailEvent.setEventType("audit.event");
    nonEmailEvent.setPayload("{}");
    nonEmailEvent.setOccurredAt(Instant.now());

    when(outboxEventRepository.findTop50ByPublishedAtIsNullOrderByOccurredAtAsc())
        .thenReturn(List.of(emailEvent, nonEmailEvent));

    int sent = dispatcher.dispatchPendingEmails();

    assertEquals(1, sent);
    assertNotNull(emailEvent.getPublishedAt());
    verify(emailSender)
        .send(
            new EmailMessage(
                "user@example.com",
                "Welcome to UrbanGate",
                "Your account is active. Welcome to UrbanGate."));
    verify(emailSender, never()).send(new EmailMessage("", "", ""));
  }
}
