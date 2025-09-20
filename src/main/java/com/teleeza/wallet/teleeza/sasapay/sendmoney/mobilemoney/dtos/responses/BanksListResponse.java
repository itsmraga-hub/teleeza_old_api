package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BanksListResponse{

	@JsonProperty("banks")
	private List<BanksItem> banks;

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private String status;
}