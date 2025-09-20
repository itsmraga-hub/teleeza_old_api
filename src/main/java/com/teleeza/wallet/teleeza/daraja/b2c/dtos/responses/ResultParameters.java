package com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResultParameters {

	@JsonProperty("ResultParameter")
	private List<ResultParameterItem> resultParameter;
}