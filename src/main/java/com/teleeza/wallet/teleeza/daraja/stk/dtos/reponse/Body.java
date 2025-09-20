package com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Body {

	@JsonProperty("stkCallback")
	private StkCallback stkCallback;
}