package com.teleeza.wallet.teleeza.sasapay.sasapay_agents.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NearestAgentsItem{

	@JsonProperty("area_name")
	private String areaName;

	@JsonProperty("distance")
	private double distance;

	@JsonProperty("latitude")
	private String latitude;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private int id;

	@JsonProperty("longitude")
	private String longitude;
}