package com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankTransferResponse {
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("TransactionReference")
    private String transactionReference;
    @JsonProperty("ReferenceNumber")
    private String referenceNumber;
}
