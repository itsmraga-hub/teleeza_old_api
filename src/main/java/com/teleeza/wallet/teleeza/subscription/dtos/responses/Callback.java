package com.teleeza.wallet.teleeza.subscription.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Callback{

	@JsonProperty("MerchantRequestID")
	private String merchantRequestID;

	@JsonProperty("CheckoutRequestID")
	private String checkoutRequestID;

	@JsonProperty("ResultDesc")
	private String resultDesc;

	@JsonProperty("ResultCode")
	private int resultCode;

	@JsonProperty("CallbackMetadata")
	private CallbackMetadata callbackMetadata;
}