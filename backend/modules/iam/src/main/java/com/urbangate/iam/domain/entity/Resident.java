// Copyright (c) UrbanGate
package com.urbangate.iam.domain.entity;

import com.urbangate.iam.domain.enums.ResidentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@ToString(exclude = {"estate"})
@Entity
@Table(
    name = "ug_residents",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_ug_residents_estate_phone",
          columnNames = {"estate_id", "phone"}),
      @UniqueConstraint(
          name = "uk_ug_residents_estate_email",
          columnNames = {"estate_id", "email"})
    })
public class Resident {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estate_id", nullable = false)
  private Estate estate;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(length = 30)
  private String phone;

  @Column(length = 200)
  private String email;

  @Column(length = 100)
  private String apartment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private ResidentStatus status = ResidentStatus.PENDING;

  @Column(name = "keycloak_user_id", length = 100)
  private String keycloakUserId;

  @Column(name = "activated_at")
  private Instant activatedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    Instant now = Instant.now();
    if (createdAt == null) {
      createdAt = now;
    }
    if (updatedAt == null) {
      updatedAt = now;
    }
    if (status == null) {
      status = ResidentStatus.PENDING;
    }
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
