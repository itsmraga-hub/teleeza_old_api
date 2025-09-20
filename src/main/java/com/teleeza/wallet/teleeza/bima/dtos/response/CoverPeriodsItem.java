package com.teleeza.wallet.teleeza.bima.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoverPeriodsItem{

	@JsonProperty("endDate")
	private String endDate;

	@JsonProperty("familyMembers")
	private int familyMembers;

	@JsonProperty("startDate")
	private String startDate;
}