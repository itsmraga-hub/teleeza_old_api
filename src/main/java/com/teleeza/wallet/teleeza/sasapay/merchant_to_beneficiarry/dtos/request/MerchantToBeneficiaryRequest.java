package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantToBeneficiaryRequest{

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

	@JsonProperty("Reason")
	private String reason;
}