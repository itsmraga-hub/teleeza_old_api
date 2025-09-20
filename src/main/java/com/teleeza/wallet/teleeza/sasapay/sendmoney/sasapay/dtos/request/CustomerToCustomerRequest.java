package com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Request payload to send money from SasaPay to SasaPay
 * accounts
 */
@Data
public class CustomerToCustomerRequest{
	@JsonProperty("TransactionReference")
	private String transactionReference;
	@JsonProperty("RecipientBeneficiaryAccountNumber")
	private String recipientBeneficiaryAccountNumber;

	@JsonProperty("SenderBeneficiaryAccountNumber")
	private String senderBeneficiaryAccountNumber;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("Amount")
	private double amount;

	@JsonProperty("CallBackUrl")
	private String callBackUrl;

	@JsonProperty("TransactionFee")
	private int transactionFee;
}