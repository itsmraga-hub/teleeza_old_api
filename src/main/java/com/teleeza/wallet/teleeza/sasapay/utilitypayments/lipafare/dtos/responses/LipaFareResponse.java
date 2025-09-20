package com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LipaFareResponse{

	@JsonProperty("MerchantReference")
	private String merchantReference;

	@JsonProperty("ReferenceNumber")
	private String referenceNumber;

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private String statusCode;
}