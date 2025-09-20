package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BanksItem{

	@JsonProperty("bank_code")
	private String bankCode;

	@JsonProperty("bank_name")
	private String bankName;
}