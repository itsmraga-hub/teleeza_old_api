package com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BankTransferRequest {
    @JsonProperty("TransactionReference")
    private String transactionReference;
    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("ReceiverNumber")
    private String receiverNumber;
    @JsonProperty("ChannelCode")
    private String channelCode;
    @JsonProperty("Amount")
    private Integer amount;
    @JsonProperty("MerchantCode")
    private String merchantCode;
    @JsonProperty("TransactionFee")
    private Integer transactionFee;
    @JsonProperty("CallBackUrl")
    private String callBackUrl;
    @JsonProperty("Reason")
    private String reason;

}
