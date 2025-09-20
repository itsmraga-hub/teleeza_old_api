package com.teleeza.wallet.teleeza.sasapay.monitor_balance.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountsItem{
	@JsonProperty("account_balance")
	private double account_balance;
	@JsonProperty("account_label")
	private String account_label;

	public double getAccount_balance() {
		return account_balance;
	}

	public String getAccount_label() {
		return account_label;
	}
}
