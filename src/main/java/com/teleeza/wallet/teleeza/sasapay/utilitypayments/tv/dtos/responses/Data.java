package com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
@lombok.Data
public class Data{

	@JsonProperty("message")
	private String message;

	@JsonProperty("status")
	private boolean status;

	public String getMessage(){
		return message;
	}

	public boolean isStatus(){
		return status;
	}
}