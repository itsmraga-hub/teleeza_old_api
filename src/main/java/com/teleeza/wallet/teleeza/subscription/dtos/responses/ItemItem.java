package com.teleeza.wallet.teleeza.subscription.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemItem{

	@JsonProperty("Value")
	private double value;

	@JsonProperty("Name")
	private String name;
}