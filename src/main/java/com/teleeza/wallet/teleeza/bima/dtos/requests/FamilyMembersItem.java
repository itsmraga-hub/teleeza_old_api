package com.teleeza.wallet.teleeza.bima.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FamilyMembersItem{

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("fullName")
	private String fullName;

	@JsonProperty("dateOfBirth")
	private String dateOfBirth;

	@JsonProperty("relationship")
	private String relationship;

	@JsonProperty("email")
	private String email;

	@JsonProperty("age")
	private int age;

}