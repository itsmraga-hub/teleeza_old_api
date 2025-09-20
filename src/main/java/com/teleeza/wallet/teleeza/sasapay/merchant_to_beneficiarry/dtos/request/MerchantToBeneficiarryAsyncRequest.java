package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantToBeneficiarryAsyncRequest{

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
	private Integer resultCode;

	@JsonProperty("TransactionDate")
	private String transactionDate;
}