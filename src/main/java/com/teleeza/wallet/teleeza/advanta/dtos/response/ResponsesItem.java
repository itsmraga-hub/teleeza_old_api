package com.teleeza.wallet.teleeza.advanta.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponsesItem{

	@JsonProperty("response-code")
	private int responseCode;

	@JsonProperty("mobile")
	private long mobile;

	@JsonProperty("messageid")
	private String messageid;

	@JsonProperty("networkid")
	private int networkid;

	@JsonProperty("response-description")
	private String responseDescription;
}