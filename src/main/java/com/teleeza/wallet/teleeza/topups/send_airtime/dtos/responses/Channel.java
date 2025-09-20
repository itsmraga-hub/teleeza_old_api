package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Channel{

	@JsonProperty("balance")
	private String balance;

	@JsonProperty("currency")
	private String currency;

	@JsonProperty("type")
	private String type;

	public void setBalance(String balance){
		this.balance = balance;
	}

	public String getBalance(){
		return balance;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}
}