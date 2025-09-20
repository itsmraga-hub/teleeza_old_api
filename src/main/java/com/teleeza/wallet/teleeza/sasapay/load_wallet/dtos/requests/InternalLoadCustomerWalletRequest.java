package com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalLoadCustomerWalletRequest {
    @JsonProperty("MobileNumber")
    private String mobileNumber;
    @JsonProperty("Amount")
    private Integer amount;
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("NetworkCode")
    private String networkCode;
}
