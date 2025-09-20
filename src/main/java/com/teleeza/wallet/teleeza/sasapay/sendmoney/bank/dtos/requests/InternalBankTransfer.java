package com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalBankTransfer {

    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    @JsonProperty("ReceiverNumber")
    private String receiverNumber;
    @JsonProperty("ChannelCode")
    private String channelCode;
    @JsonProperty("Amount")
    private Integer amount;
    @JsonProperty("Reason")
    private String reason;
}
