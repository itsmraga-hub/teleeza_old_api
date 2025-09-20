package com.teleeza.wallet.teleeza.subscription.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Body{

	@JsonProperty("Callback")
	private Callback callback;
}