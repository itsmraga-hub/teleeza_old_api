package com.teleeza.wallet.teleeza.bima.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CustomerItem {

	@JsonProperty("address")
	private String address;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("documentType")
	private String documentType;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("fullName")
	private String fullName;

	@JsonProperty("dateOfBirth")
	private String dateOfBirth;

	@JsonProperty("idNumber")
	private String idNumber;

	@JsonProperty("externalRef")
	private String externalRef;

	@JsonProperty("email")
	private String email;
}