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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"estate", "resident"})
@Entity
@Table(
    name = "ug_activation_codes",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_ug_activation_codes_code",
          columnNames = {"code"})
    })
public class ActivationCode {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estate_id", nullable = false)
  private Estate estate;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "resident_id", nullable = false)
  private Resident resident;

  @Column(nullable = false, length = 50)
  private String code;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "used_at")
  private Instant usedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
