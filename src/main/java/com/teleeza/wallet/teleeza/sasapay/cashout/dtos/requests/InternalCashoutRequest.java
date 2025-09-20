package com.teleeza.wallet.teleeza.sasapay.cashout.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalCashoutRequest {
    @JsonProperty("Amount")
    private int amount;

    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    @JsonProperty("SasaPayAgentNumber")
    private int sasaPayAgentNumber;
}
