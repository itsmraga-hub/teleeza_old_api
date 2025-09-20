package com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerToCustomerAsyncResponse {
    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;

    @JsonProperty("BillRefNumber")
    private String billRefNumber;

    @JsonProperty("SenderCustomerMobile")
    private String senderCustomerMobile;

    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;

    @JsonProperty("ResultDesc")
    private String resultDesc;

    @JsonProperty("TransAmount")
    private String transAmount;

    @JsonProperty("ReceiverCustomerMobile")
    private String receiverCustomerMobile;

    @JsonProperty("ResultCode")
    private int resultCode;

    @JsonProperty("TransactionDate")
    private String transactionDate;
}
