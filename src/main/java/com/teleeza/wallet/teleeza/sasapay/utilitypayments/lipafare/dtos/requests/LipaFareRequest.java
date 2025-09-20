package com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LipaFareRequest{

	@JsonProperty("BillRefNumber")
	private String billRefNumber;

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("TransactionReference")
	private String transactionReference;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("Amount")
	private int amount;

	@JsonProperty("MatatuBillNumber")
	private int matatuBillNumber;

	@JsonProperty("CallBackUrl")
	private String callBackUrl;

	@JsonProperty("TransactionFee")
	private int transactionFee;

	@JsonProperty("Reason")
	private String reason;
}