package com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TillPaymentRequest{
	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;
	@JsonProperty("TransactionReference")
	private String transactionReference;
	@JsonProperty("SasaPayBillNumber")
	private int sasaPayBillNumber;
	@JsonProperty("MerchantCode")
	private String merchantCode;
	@JsonProperty("Amount")
	private Integer amount;
	@JsonProperty("CallBackUrl")
	private String callBackUrl;
	@JsonProperty("TransactionFee")
	private int transactionFee;
	@JsonProperty("Reason")
	private String reason;
}