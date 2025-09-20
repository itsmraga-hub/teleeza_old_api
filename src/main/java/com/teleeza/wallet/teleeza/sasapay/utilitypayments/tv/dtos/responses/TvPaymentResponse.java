package com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
@lombok.Data
public class TvPaymentResponse{

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private String statusCode;


	public String getStatusCode(){
		return statusCode;
	}
}