package com.teleeza.wallet.teleeza.subscription.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubscriptionRequest {
    @JsonProperty("TransactionReference")
    private String transactionReference;
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("SasaPayBillNumber")
    private Long sasaPayBillNumber;
    @JsonProperty("Amount")
    private String amount;
    @JsonProperty("MerchantCode")
    private String merchantCode;
    @JsonProperty("TransactionFee")
    private Integer transactionFee;
    @JsonProperty("Reason")
    private String reason;
    @JsonProperty("CallBackUrl")
    private String callBackUrl;
}
