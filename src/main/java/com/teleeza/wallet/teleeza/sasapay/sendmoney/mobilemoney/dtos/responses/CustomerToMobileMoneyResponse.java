package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerToMobileMoneyResponse {
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("TransactionReference")
    private String transactionReference;
    @JsonProperty("ReferenceNumber")
    private String referenceNumber;
//    @JsonProperty("MerchantReference")
//    private String merchantReference;
}
