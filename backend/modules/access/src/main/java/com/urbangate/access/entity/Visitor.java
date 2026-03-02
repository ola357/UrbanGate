// Copyright (c) UrbanGate
package com.urbangate.access.entity;

import com.urbangate.shared.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Visitor extends BaseEntity {
  private String accessCode;
  private String name;
  private String phone;
  private String email;
  private String visitorType;
}
