package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileMoneyTransferAsyncRequest{
	@JsonProperty("MerchantRequestID")
	private String merchantRequestID;
	@JsonProperty("MerchantTransactionReference")
	private String merchantTransactionReference;
	@JsonProperty("SourceChannel")
	private String sourceChannel;
	@JsonProperty("DestinationChannel")
	private String destinationChannel;
	@JsonProperty("RecipientAccountNumber")
	private String recipientAccountNumber;
	@JsonProperty("TransactionAmount")
	private String transactionAmount;
	@JsonProperty("MerchantAccountBalance")
	private String merchantAccountBalance;
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