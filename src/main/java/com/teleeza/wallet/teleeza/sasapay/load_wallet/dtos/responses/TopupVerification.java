package com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopupVerification{

	@JsonProperty("error")
	private boolean error;

	@JsonProperty("message")
	private String message;
}