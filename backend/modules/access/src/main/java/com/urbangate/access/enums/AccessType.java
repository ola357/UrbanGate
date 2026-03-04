// Copyright (c) UrbanGate
package com.urbangate.access.enums;

import lombok.Getter;

public enum AccessType {
  SINGLE_ACCESS(
      "One-Single Pass for single guest, Expires after after single entry or on your set time"),
  MULTIPLE_PASS_ACCESS(
      "Used for same visitor to enter multiple times, Ideal for short stays, staff and maintenance"),
  GROUP_ACCESS(
      "One Pass for multiple quests arriving at this same time, Ideal for small gatherings and family visits"),
  ALL_GATE_ACCESS(
      "Generates a separate pass for each entrance, Use when visitors may enter through different gates");

  @Getter private final String description;

  AccessType(String description) {
    this.description = description;
  }
}
