package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalMobileMoneyTransferRequest {
    @JsonProperty("MobileOperatorNumber")
    private String mobileOperatorNumber;

    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    @JsonProperty("Amount")
    private Integer amount;

    @JsonProperty("ChannelCode")
    private String channelCode;
}
