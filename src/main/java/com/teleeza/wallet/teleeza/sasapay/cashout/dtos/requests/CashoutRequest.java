package com.teleeza.wallet.teleeza.sasapay.cashout.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CashoutRequest{

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("TransactionReference")
	private String transactionReference;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("Amount")
	private Integer amount;

	@JsonProperty("CallBackUrl")
	private String callBackUrl;

	@JsonProperty("TransactionFee")
	private int transactionFee;

	@JsonProperty("SasaPayAgentNumber")
	private int sasaPayAgentNumber;

	@JsonProperty("Reason")
	private String reason;
}