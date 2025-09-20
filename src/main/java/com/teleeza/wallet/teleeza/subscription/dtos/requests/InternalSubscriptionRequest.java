package com.teleeza.wallet.teleeza.subscription.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalSubscriptionRequest {
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("SasaPayBillNumber")
    private Long sasaPayBillNumber;
    @JsonProperty("Amount")
    private Integer amount;
    // added fields
//    private String planeName;
//    private String planId;
    private String referredByCode;

}
