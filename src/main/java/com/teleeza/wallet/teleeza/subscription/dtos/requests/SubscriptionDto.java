package com.teleeza.wallet.teleeza.subscription.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionDto {
    private String planName;
    private String planId;
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccNo;
    @JsonProperty("Amount")
    private String amount;
    @JsonProperty("SasaPayBillNumber")
    private Long payBillNo;
    private String referredByCode;
//    private String organisation;
}
