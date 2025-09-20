package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer{

	@JsonProperty("display_name")
	private String displayName;

	@JsonProperty("mobile_number")
	private String mobileNumber;

	@JsonProperty("aml_score")
	private int amlScore;
}