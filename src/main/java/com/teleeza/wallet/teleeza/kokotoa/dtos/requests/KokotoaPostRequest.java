package com.teleeza.wallet.teleeza.kokotoa.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KokotoaPostRequest{

	@JsonProperty("date")
	private String date;

	@JsonProperty("amount")
	private double amount;

	@JsonProperty("name")
	private String name;

	@JsonProperty("mobile")
	private String mobile;

	@JsonProperty("description")
	private String description;

	@JsonProperty("category")
	private String category;
}