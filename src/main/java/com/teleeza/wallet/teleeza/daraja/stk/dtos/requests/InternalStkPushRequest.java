package com.teleeza.wallet.teleeza.daraja.stk.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalStkPushRequest {

	@JsonProperty("Amount")
	private String amount;

	@JsonProperty("PhoneNumber")
	private String phoneNumber;

	@JsonProperty("AccountReference")
	private String accountReference;

	private String referredByCode;

	private String subscriptionPlan;



}