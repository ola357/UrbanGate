// Copyright (c) UrbanGate
package com.urbangate.iam.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxPublisher {
  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;

  public OutboxPublisher(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
    this.outboxEventRepository = outboxEventRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public OutboxEvent publish(
      String aggregateType, String aggregateId, String eventType, Map<String, Object> payload) {
    String json = toJson(payload);

    OutboxEvent event = new OutboxEvent();
    event.setId(UUID.randomUUID());
    event.setAggregateType(aggregateType);
    event.setAggregateId(aggregateId);
    event.setEventType(eventType);
    event.setPayload(json);
    event.setOccurredAt(Instant.now());

    return outboxEventRepository.save(event);
  }

  private String toJson(Map<String, Object> payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize outbox payload", e);
    }
  }
}
