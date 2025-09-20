package com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AirtimeRequest{
	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;
	@JsonProperty("MobileNumber")
	private String mobileNumber;
	@JsonProperty("MerchantCode")
	private String merchantCode;
	@JsonProperty("Amount")
	private int amount;
	@JsonProperty("CallBackUrl")
	private String callBackUrl;
	@JsonProperty("NetworkCode")
	private String networkCode;
}