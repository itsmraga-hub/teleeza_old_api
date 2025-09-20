package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendAirtimeRequest{

	@JsonProperty("country")
	private String country;

	@JsonProperty("adTitle")
	private String adTitle;

	@JsonProperty("note")
	private String note;

	@JsonProperty("amount")
	private String amount;

	@JsonProperty("name")
	private String name;

	@JsonProperty("mobile")
	private String mobile;

	@JsonProperty("account")
	private String account;

	@JsonProperty("reference")
	private String reference;


	@JsonProperty("currency")
	private String currency;

	@JsonProperty("email")
	private String email;

	@JsonProperty("network")
	private String network;

	@JsonProperty("ad_type")
	private String adType;


	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setNote(String note){
		this.note = note;
	}

	public String getNote(){
		return note;
	}

	public void setAmount(String amount){
		this.amount = amount;
	}

	public String getAmount(){
		return amount;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return mobile;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setNetwork(String network){
		this.network = network;
	}

	public String getNetwork(){
		return network;
	}

	@Override
	public String toString() {
		return "SendAirtimeRequest{" +
				"country='" + country + '\'' +
				", note='" + note + '\'' +
				", amount='" + amount + '\'' +
				", name='" + name + '\'' +
				", mobile='" + mobile + '\'' +
				", currency='" + currency + '\'' +
				", email='" + email + '\'' +
				", network='" + network + '\'' +
				'}';
	}
}