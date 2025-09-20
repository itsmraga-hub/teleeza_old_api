package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerConfirmationRequest{

	@JsonProperty("RegistrationRequestId")
	private String registrationRequestId;

	@JsonProperty("ConfirmationCode")
	private String confirmationCode;

	@JsonProperty("MerchantCode")
	private String merchantCode;

//	public CustomerConfirmationRequest() {
//	}
//
//	public CustomerConfirmationRequest(String registrationRequestId, String confirmationCode, String merchantCode) {
//		this.registrationRequestId = registrationRequestId;
//		this.confirmationCode = confirmationCode;
//		this.merchantCode = merchantCode;
//	}
}