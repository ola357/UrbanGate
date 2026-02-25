// Copyright (c) UrbanGate
package com.urbangate.iam.entity;

import com.urbangate.shared.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class ActivationCode extends BaseEntity {
  private String code;
  private int ttlInHours;
  private boolean isRevoked;
  private String userId;
}
