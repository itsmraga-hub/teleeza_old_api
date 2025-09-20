package com.teleeza.wallet.teleeza.sasapay.monitor_balance.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@lombok.Data
public class Data{
	@JsonProperty("CurrencyCode")
	private String CurrencyCode;
	@JsonProperty("OrgAccountBalance")
	private double OrgAccountBalance;
	@JsonProperty("Accounts")
	private List<AccountsItem> Accounts;
}