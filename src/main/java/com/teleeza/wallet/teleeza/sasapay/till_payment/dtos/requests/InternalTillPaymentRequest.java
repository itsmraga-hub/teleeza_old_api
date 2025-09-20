package com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalTillPaymentRequest {
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("SasaPayBillNumber")
    private Integer sasaPayBillNumber;
    @JsonProperty("Amount")
    private Integer amount;
}
