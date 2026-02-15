package com.estateresource.estatemanger.backoffice.dto.request;

import com.estateresource.estatemanger.shared.enums.PayableBills;

import java.util.List;

public record EstateOnboardingRequest(
         String name, //configurable
         String description,
         String icon,
         Long creator,
         String address,
         String state,
         String phone,
         int numberOfDaysBeforeOverdue,
         int numberOfDaysBeforeUpcomingPayment,
         String estateCode,
         boolean sendBirthdayShout,
         int maximumGuestsForMultipleCode,
         List<PayableBills>payableBills
) {
}
