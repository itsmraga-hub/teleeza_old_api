package com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data{

	@JsonProperty("message")
	private String message;

	@JsonProperty("status")
	private boolean status;

	@JsonProperty("statusCode")
	private String statusCode;

}