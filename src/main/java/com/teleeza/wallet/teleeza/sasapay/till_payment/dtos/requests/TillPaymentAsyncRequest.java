package com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TillPaymentAsyncRequest{

	@JsonProperty("MerchantRequestID")
	private String merchantRequestID;

	@JsonProperty("MerchantTransactionReference")
	private String merchantTransactionReference;

	@JsonProperty("SourceChannel")
	private String sourceChannel;

	@JsonProperty("DestinationChannel")
	private String destinationChannel;

	@JsonProperty("MerchantAccountBalance")
	private String merchantAccountBalance;

	@JsonProperty("RecipientAccountNumber")
	private String recipientAccountNumber;

	@JsonProperty("TransactionAmount")
	private String transactionAmount;

	@JsonProperty("SenderAccountNumber")
	private String senderAccountNumber;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("CheckoutRequestID")
	private String checkoutRequestID;

	@JsonProperty("RecipientName")
	private String recipientName;

	@JsonProperty("ResultDesc")
	private String resultDesc;

	@JsonProperty("SasaPayTransactionID")
	private String sasaPayTransactionID;

	@JsonProperty("ResultCode")
	private int resultCode;

	@JsonProperty("TransactionDate")
	private String transactionDate;
}