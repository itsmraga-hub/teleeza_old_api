package com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillPaymentResponse {
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("MerchantReference")
    private String merchantReference;
    @JsonProperty("ReferenceNumber")
    private String referenceNumber;

    public BillPaymentResponse() {
    }

    public BillPaymentResponse(String statusCode, String message, String merchantReference, String referenceNumber) {
        this.statusCode = statusCode;
        this.message = message;
        this.merchantReference = merchantReference;
        this.referenceNumber = referenceNumber;
    }
}


