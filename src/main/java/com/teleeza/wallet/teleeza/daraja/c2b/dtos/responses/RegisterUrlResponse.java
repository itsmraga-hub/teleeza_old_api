package com.teleeza.wallet.teleeza.daraja.c2b.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterUrlResponse {

	@JsonProperty("ConversationID")
	private String conversationID;

	@JsonProperty("ResponseDescription")
	private String responseDescription;

	@JsonProperty("OriginatorCoversationID")
	private String originatorCoversationID;
	@JsonProperty("ResponseCode")
	private String ResponseCode;
}