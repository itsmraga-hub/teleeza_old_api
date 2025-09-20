package com.teleeza.wallet.teleeza.subscription.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionResponse {

//    private String referralCode;
//    private String beneficiaryAccountNumber;
//    private Integer amount;
//    private String planId;
//    private String planName;

    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("MerchantReference")
    private String merchantReference;
    @JsonProperty("ReferenceNumber")
    private String referenceNumber;

//    public SubscriptionResponse() {
//    }

//    public SubscriptionResponse(String statusCode, String message, String merchantReference, String referenceNumber) {
//        this.statusCode = statusCode;
//        this.message = message;
//        this.merchantReference = merchantReference;
//        this.referenceNumber = referenceNumber;
//    }
}