package com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopUpAsyncRequest {
    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;
    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;
    @JsonProperty("ResultCode")
    private int resultCode;
    @JsonProperty("ResultDesc")
    private String resultDesc;
    @JsonProperty("TransAmount")
    private String transAmount;
    @JsonProperty("BillRefNumber")
    private String billRefNumber;
    @JsonProperty("TransactionDate")
    private String transactionDate;
    @JsonProperty("CustomerMobile")
    private String customerMobile;
    @JsonProperty("ThirdPartyTransID")
    private String thirdPartyTransID;
    @JsonProperty("SenderCustomerMobile")
    private String senderCustomerMobile;
    @JsonProperty("ReceiverCustomerMobile")
    private String receiverCustomerMobile;

}