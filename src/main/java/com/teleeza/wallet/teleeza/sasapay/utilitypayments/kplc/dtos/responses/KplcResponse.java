package com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
@lombok.Data
public class KplcResponse{
	@JsonProperty("message")
	private String message;
	@JsonProperty("statusCode")
	private String statusCode;

}