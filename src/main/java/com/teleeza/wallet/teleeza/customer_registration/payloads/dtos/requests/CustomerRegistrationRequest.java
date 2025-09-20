package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRegistrationRequest {
//	@JsonProperty("MerchantCode")
//	private String merchantCode;
	@JsonProperty("FirstName")
	private String firstName;
//	@JsonProperty("MiddleName")
//	private String middleName;
	@JsonProperty("LastName")
	private String lastName;
//	@JsonProperty("CountryCode")
//	private String countryCode;
	@JsonProperty("MobileNumber")
	private String mobileNumber;
//	@JsonProperty("DocumentType")
//	private String documentType;
//	@JsonProperty("DocumentNumber")
//	private String documentNumber;
	@JsonProperty("Email")
	private String email;

	// added fields
	private String dob;
	private String gender;
	private Integer age;
	private String location;
	private Boolean isPolicyAccepted;
	private String referredByCode;
	private String pin;
	//private String referralCode;


}