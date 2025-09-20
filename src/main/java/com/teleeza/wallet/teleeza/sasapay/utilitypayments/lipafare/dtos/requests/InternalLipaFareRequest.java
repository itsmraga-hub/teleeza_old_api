package com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalLipaFareRequest {
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("Amount")
    private int amount;
    @JsonProperty("MatatuBillNumber")
    private int matatuBillNumber;
}
