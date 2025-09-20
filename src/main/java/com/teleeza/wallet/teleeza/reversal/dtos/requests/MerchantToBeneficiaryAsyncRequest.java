package com.teleeza.wallet.teleeza.reversal.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MerchantToBeneficiaryAsyncRequest {

	@JsonProperty("MerchantRequestID")
	private String merchantRequestID;

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("SenderAccountNumber")
	private String senderAccountNumber;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("CheckoutRequestID")
	private String checkoutRequestID;

	@JsonProperty("ResultDesc")
	private String resultDesc;

	@JsonProperty("TransAmount")
	private String transAmount;

	@JsonProperty("ResultCode")
	private String resultCode;

	@JsonProperty("TransactionDate")
	private String transactionDate;
}