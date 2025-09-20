package com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class B2CTransactionAsyncResponse {

	@JsonProperty("Result")
	private Result result;
}