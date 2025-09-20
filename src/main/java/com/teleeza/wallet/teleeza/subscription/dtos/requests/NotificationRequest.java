package com.teleeza.wallet.teleeza.subscription.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationRequest{

	@JsonProperty("apikey")
	private String apikey;

	@JsonProperty("mobile")
	private String mobile;

	@JsonProperty("partnerID")
	private String partnerID;

	@JsonProperty("message")
	private String message;

	@JsonProperty("shortcode")
	private String shortcode;
}