package com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class B2CTransactionResponse {

	@JsonProperty("ConversationID")
	private String conversationID;

	@JsonProperty("ResponseCode")
	private String responseCode;

	@JsonProperty("OriginatorConversationID")
	private String originatorConversationID;

	@JsonProperty("ResponseDescription")
	private String responseDescription;
}