// Copyright (c) UrbanGate
package com.urbangate.iam.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"resident"})
@Entity
@Table(name = "ug_login_attempts")
public class LoginAttempt {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "resident_id", nullable = false)
  private Resident resident;

  @Column(name = "attempted_at", nullable = false)
  private Instant attemptedAt;

  @Column(nullable = false)
  private boolean success;

  @Column(name = "failure_reason", length = 200)
  private String failureReason;

  @Column(name = "ip_address", length = 64)
  private String ipAddress;

  @Column(name = "user_agent", length = 255)
  private String userAgent;

  @PrePersist
  void prePersist() {
    if (attemptedAt == null) {
      attemptedAt = Instant.now();
    }
  }
}
