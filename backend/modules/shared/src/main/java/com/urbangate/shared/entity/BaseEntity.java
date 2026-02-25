// Copyright (c) UrbanGate
package com.urbangate.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.urbangate.shared.enums.EntityStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Timestamp createdOn;

  private Timestamp lastModifiedOn;

  @Enumerated(EnumType.STRING)
  private EntityStatus entityStatus;

  public void prepareForInsert() {
    Timestamp now = Timestamp.from(Instant.now());

    if (this.createdOn == null) {
      this.createdOn = now;
    }

    if (this.lastModifiedOn == null) {
      this.lastModifiedOn = now;
    }

    if (this.entityStatus == null) {
      this.entityStatus = EntityStatus.ACTIVE;
    }
  }

  public void prepareForUpdate() {
    this.lastModifiedOn = Timestamp.from(Instant.now());
  }
}
