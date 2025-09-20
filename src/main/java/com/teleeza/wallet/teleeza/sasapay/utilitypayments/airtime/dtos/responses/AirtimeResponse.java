package com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
@lombok.Data
public class AirtimeResponse{
	@JsonProperty("message")
	private String message;
	@JsonProperty("statusCode")
	private String statusCode;
}