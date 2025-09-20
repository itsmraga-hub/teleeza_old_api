package com.teleeza.wallet.teleeza.sasapay.monitor_balance.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
@lombok.Data
public class BalanceResponse{
	@JsonProperty("data")
	private Data data;
	@JsonProperty("message")
	private String message;
	@JsonProperty("statusCode")
	private String statusCode;
}
