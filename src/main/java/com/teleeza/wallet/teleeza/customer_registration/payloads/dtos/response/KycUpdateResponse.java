package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KycUpdateResponse{

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private int statusCode;
}