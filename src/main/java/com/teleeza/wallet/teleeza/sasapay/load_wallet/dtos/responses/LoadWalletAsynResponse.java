package com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoadWalletAsynResponse{

	@JsonProperty("MerchantRequestID")
	private String merchantRequestID;

	@JsonProperty("BillRefNumber")
	private String billRefNumber;

	@JsonProperty("CustomerMobile")
	private String customerMobile;

	@JsonProperty("CheckoutRequestID")
	private String checkoutRequestID;

	@JsonProperty("ResultDesc")
	private String resultDesc;

	@JsonProperty("TransAmount")
	private String transAmount;

	@JsonProperty("ThirdPartyTransID")
	private String thirdPartyTransID;

	@JsonProperty("ResultCode")
	private int resultCode;

	@JsonProperty("TransactionDate")
	private String transactionDate;

}