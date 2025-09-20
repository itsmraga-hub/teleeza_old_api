package com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostPaidBillPaymentRequest{

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("MobileNumber")
	private String mobileNumber;

	@JsonProperty("ServiceCode")
	private String serviceCode;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("Currency")
	private String currency;

	@JsonProperty("Amount")
	private int amount;

	@JsonProperty("CallBackUrl")
	private String callBackUrl;

	@JsonProperty("AccountNumber")
	private String accountNumber;
}