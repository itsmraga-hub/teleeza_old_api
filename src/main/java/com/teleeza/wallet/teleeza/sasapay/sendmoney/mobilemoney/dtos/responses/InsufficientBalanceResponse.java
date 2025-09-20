package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsufficientBalanceResponse {

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private String statusCode;
}