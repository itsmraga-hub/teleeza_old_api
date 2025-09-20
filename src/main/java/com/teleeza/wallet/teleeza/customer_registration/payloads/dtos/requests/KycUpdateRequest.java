package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KycUpdateRequest{

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("Email")
	private String email;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("FirstName")
	private String firstName;

	@JsonProperty("DocumentType")
	private String documentType;

	@JsonProperty("LastName")
	private String lastName;

	@JsonProperty("MiddleName")
	private String middleName;

	@JsonProperty("DocumentNumber")
	private String documentNumber;
}