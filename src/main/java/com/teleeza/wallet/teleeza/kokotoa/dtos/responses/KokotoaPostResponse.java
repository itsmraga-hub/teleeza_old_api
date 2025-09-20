package com.teleeza.wallet.teleeza.kokotoa.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KokotoaPostResponse{

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private int statusCode;
}