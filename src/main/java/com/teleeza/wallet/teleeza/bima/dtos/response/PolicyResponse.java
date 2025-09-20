package com.teleeza.wallet.teleeza.bima.dtos.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyResponse{

	@JsonProperty("customerFullName")
	private String customerFullName;

	@JsonProperty("customerAddress")
	private String customerAddress;

	@JsonProperty("nextOfKin")
	private List<NextOfKinItem> nextOfKin;

	@JsonProperty("endDate")
	private String endDate;

	@JsonProperty("available")
	private double available;

	@JsonProperty("policyNumber")
	private String policyNumber;

	@JsonProperty("totalPayments")
	private double totalPayments;

	@JsonProperty("customerDateOfBirth")
	private String customerDateOfBirth;

	@JsonProperty("familyMembers")
	private List<FamilyMembersItem> familyMembers;

	@JsonProperty("customerIdNumber")
	private String customerIdNumber;

	@JsonProperty("customerExternalRef")
	private String customerExternalRef;

	@JsonProperty("premium")
	private double premium;

	@JsonProperty("customerId")
	private int customerId;

	@JsonProperty("customerMobileNumber")
	private String customerMobileNumber;

	@JsonProperty("id")
	private int id;

	@JsonProperty("coverPeriods")
	private List<CoverPeriodsItem> coverPeriods;

	@JsonProperty("productPackId")
	private int productPackId;

	@JsonProperty("startDate")
	private String startDate;

	@JsonProperty("status")
	private String status;
}