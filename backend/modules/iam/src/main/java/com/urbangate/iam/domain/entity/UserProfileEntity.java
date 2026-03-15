// Copyright (c) UrbanGate
package com.urbangate.iam.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
    name = "ug_user_profiles",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_ug_user_profiles_subject",
          columnNames = {"subject"}),
      @UniqueConstraint(
          name = "uk_ug_user_profiles_email",
          columnNames = {"email"})
    })
public class UserProfileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(nullable = false, length = 200)
  private String subject;

  @Column(nullable = false, length = 200)
  private String email;

  @Column(name = "display_name", length = 200)
  private String displayName;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static UserProfileEntity create(String subject, String email, String displayName) {
    UserProfileEntity entity = new UserProfileEntity();
    entity.setSubject(subject);
    entity.setEmail(email);
    entity.setDisplayName(displayName);
    entity.touch();
    if (entity.getCreatedAt() == null) {
      entity.setCreatedAt(entity.getUpdatedAt());
    }
    return entity;
  }

  public void touch() {
    Instant now = Instant.now();
    if (createdAt == null) {
      createdAt = now;
    }
    updatedAt = now;
  }

  @PrePersist
  void prePersist() {
    touch();
  }

  @PreUpdate
  void preUpdate() {
    touch();
  }
}
