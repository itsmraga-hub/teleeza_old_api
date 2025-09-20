package com.teleeza.wallet.teleeza.reversal.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayBillsReversalAsyncRequest{

	@JsonProperty("MerchantRequestID")
	private String merchantRequestID;

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("ReceiverMerchantCode")
	private String receiverMerchantCode;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("CheckoutRequestID")
	private String checkoutRequestID;

	@JsonProperty("ResultDesc")
	private String resultDesc;

	@JsonProperty("TransAmount")
	private String transAmount;

	@JsonProperty("SenderMerchantCode")
	private String senderMerchantCode;

	@JsonProperty("ResultCode")
	private int resultCode;

	@JsonProperty("TransactionDate")
	private String transactionDate;
}