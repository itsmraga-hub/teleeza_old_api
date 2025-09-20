package com.teleeza.wallet.teleeza.sasapay.sasapay_agents.dtos.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AgentsResponse{

	@JsonProperty("nearest_agents")
	private List<NearestAgentsItem> nearestAgents;

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private String statusCode;
}