package com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalTvPaymentRequest {
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    @JsonProperty("MobileNumber")
    private String mobileNumber;

    @JsonProperty("ServiceCode")
    private String serviceCode;

    @JsonProperty("Amount")
    private int amount;

    @JsonProperty("AccountNumber")
    private String accountNumber;
}
