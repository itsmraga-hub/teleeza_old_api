package com.teleeza.wallet.teleeza.daraja.b2c.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalB2CTransactionRequest {

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("Amount")
	private String amount;

	@JsonProperty("Occassion")
	private String occassion;

	@JsonProperty("CommandID")
	private String commandID;

	@JsonProperty("PartyB")
	private String partyB;
}