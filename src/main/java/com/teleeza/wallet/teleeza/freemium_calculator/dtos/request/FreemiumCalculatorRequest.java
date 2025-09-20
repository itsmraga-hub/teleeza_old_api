package com.teleeza.wallet.teleeza.freemium_calculator.dtos.request;

import lombok.Data;

@Data
public class FreemiumCalculatorRequest {
    private String subscriptionPlan;
    private Integer additionalFamilyMembers;
}
