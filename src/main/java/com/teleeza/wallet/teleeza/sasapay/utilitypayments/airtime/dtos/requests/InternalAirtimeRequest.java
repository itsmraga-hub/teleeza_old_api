package com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalAirtimeRequest {

    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("MobileNumber")
    private String mobileNumber;
    @JsonProperty("Amount")
    private int amount;
    @JsonProperty("NetworkCode")
    private String networkCode;
}
