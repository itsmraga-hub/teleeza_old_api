package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class  CustomerExistsResponse{

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private String statusCode;

//	@JsonProperty("Result")
//	private Result result;
}