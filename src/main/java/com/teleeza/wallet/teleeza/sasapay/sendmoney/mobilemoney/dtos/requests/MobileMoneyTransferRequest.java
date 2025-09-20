package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MobileMoneyTransferRequest{

	@JsonProperty("BeneficiaryAccountNumber")
	private String recipientBeneficiaryAccNumber;

	@JsonProperty("TransactionReference")
	private String transactionReference;

	@JsonProperty("MobileOperatorNumber")
	private String mobileOperatorNumber;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("Amount")
	private double amount;

	@JsonProperty("CallBackUrl")
	private String callBackUrl;

	@JsonProperty("ChannelCode")
	private String channelCode;

	@JsonProperty("TransactionFee")
	private int transactionFee;

	@JsonProperty("Reason")
	private String reason;
}