// Copyright (c) UrbanGate
package com.urbangate.iam.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

  @Mock private OutboxEventRepository outboxEventRepository;

  private OutboxPublisher publisher;

  @BeforeEach
  void setUp() {
    publisher = new OutboxPublisher(outboxEventRepository, new ObjectMapper());
  }

  @Test
  void publishCreatesOutboxEvent() {
    when(outboxEventRepository.save(any(OutboxEvent.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    OutboxEvent event =
        publisher.publish("Resident", "123", "email.resident_onboarded", Map.of("key", "value"));

    ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
    verify(outboxEventRepository).save(captor.capture());

    OutboxEvent saved = captor.getValue();
    assertNotNull(saved.getId());
    assertEquals("Resident", saved.getAggregateType());
    assertEquals("123", saved.getAggregateId());
    assertEquals("email.resident_onboarded", saved.getEventType());
    assertNotNull(saved.getPayload());
    assertNotNull(saved.getOccurredAt());
    assertEquals(saved, event);
  }
}
