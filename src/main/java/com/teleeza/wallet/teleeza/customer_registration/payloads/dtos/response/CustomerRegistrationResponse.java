package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRegistrationResponse {

	@JsonProperty("RequestId")
	private String requestId;

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private String statusCode;

	@SneakyThrows
	@Override
	public String toString() {
		return new ObjectMapper().writeValueAsString(this);
	}

	public CustomerRegistrationResponse(String requestId, String message, String statusCode) {
		this.requestId = requestId;
		this.message = message;
		this.statusCode = statusCode;
	}
}