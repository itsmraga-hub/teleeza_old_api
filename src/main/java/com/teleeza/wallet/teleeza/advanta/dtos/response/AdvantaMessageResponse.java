package com.teleeza.wallet.teleeza.advanta.dtos.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdvantaMessageResponse{

	@JsonProperty("responses")
	private List<ResponsesItem> responses;
}