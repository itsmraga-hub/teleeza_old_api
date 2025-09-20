package com.teleeza.wallet.teleeza.subscription.dtos;

import lombok.Data;

@Data
public class SubscriptionDto {
    private String referralCode;
    private String beneficiaryAccountNumber;
    private int amount;
    private String planId;

}
