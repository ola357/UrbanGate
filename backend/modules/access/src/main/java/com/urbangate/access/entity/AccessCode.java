// Copyright (c) UrbanGate
package com.urbangate.access.entity;

import com.urbangate.access.enums.AccessType;
import com.urbangate.shared.entity.BaseEntity;
import jakarta.persistence.Entity;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class AccessCode extends BaseEntity {
  private String code;
  private String description;
  private AccessType accessType;
  private boolean active;
  private Timestamp expiryTime;
  private String userId;
  private String purposeOfVisit;
  private int noOfGuests;
  private Timestamp startTime;
  private String groupName;
  private String realm;
}
