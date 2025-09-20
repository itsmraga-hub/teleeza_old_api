package com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoadCustomerWalletRequest{

	@JsonProperty("CurrencyCode")
	private String currencyCode;

	@JsonProperty("MobileNumber")
	private String mobileNumber;

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("TransactionReference")
	private String transactionReference;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("Amount")
	private int amount;

	@JsonProperty("CallBackUrl")
	private String callBackUrl;

	@JsonProperty("NetworkCode")
	private String networkCode;

	@JsonProperty("Reason")
	private String reason;
}