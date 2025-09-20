package com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TvPaymentRequest{

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("MobileNumber")
	private String mobileNumber;

	@JsonProperty("ServiceCode")
	private String serviceCode;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("Amount")
	private int amount;

	@JsonProperty("CallBackUrl")
	private String callBackUrl;

	@JsonProperty("AccountNumber")
	private String accountNumber;
}