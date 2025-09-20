package com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalCustomerToCustomerRequest {
//    @JsonProperty("RecipientBeneficiaryAccountNumber")
//    private String recipientBeneficiaryAccountNumber;

    @JsonProperty("SenderBeneficiaryAccountNumber")
    private String senderBeneficiaryAccountNumber;

    @JsonProperty("Amount")
    private Integer amount;
    private String recipientPhoneNumber;
}
