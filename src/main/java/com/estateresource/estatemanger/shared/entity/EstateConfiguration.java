package com.estateresource.estatemanger.shared.entity;

import com.estateresource.estatemanger.shared.enums.PayableBills;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class EstateConfiguration extends BaseEntity {

    private Long id;
    private String estateId;
    private int numberOfDaysBeforeOverdue;
    private int numberOfDaysBeforeUpcomingPayment;
    private String estateCode;
    private boolean sendBirthdayShout;
    private int maximumGuestsForMultipleCode;
    private List<PayableBills> payableBills;

}
