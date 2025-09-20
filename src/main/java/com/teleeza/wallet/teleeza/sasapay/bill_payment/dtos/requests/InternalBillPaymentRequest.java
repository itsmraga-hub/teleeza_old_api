package com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalBillPaymentRequest {
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("SasaPayBillNumber")
    private Long sasaPayBillNumber;
    @JsonProperty("Amount")
    private int amount;

}
