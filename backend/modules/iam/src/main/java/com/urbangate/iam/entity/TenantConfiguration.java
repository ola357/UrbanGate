// Copyright (c) UrbanGate
package com.urbangate.iam.entity;

import com.urbangate.shared.entity.BaseEntity;
import com.urbangate.shared.enums.PayableBills;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TenantConfiguration extends BaseEntity {

  private Long id;
  private String name; // configurable
  private String description;
  private String icon;
  private String creator;
  private String address;
  private String state;
  private String phone;
  private String realm;

  private int numberOfDaysBeforeOverdue;
  private int numberOfDaysBeforeUpcomingPayment;
  private String estateCode;
  private boolean sendBirthdayShout;
  private int maximumGuestsForMultipleCode;
  private List<PayableBills> payableBills;
}
