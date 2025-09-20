package com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalPostPaidBillPaymentRequest {
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    @JsonProperty("MobileNumber")
    private String mobileNumber;

    @JsonProperty("ServiceCode")
    private String serviceCode;

//    @JsonProperty("Currency")
//    private String currency;

    @JsonProperty("Amount")
    private int amount;


    @JsonProperty("AccountNumber")
    private String accountNumber;
}
