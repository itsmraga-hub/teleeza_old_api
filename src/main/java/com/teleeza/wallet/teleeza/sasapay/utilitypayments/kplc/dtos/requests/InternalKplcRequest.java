package com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalKplcRequest {
    @JsonProperty("MeterNumber")
    private String meterNumber;

    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    @JsonProperty("MobileNumber")
    private String mobileNumber;

    @JsonProperty("Amount")
    private int amount;

}
