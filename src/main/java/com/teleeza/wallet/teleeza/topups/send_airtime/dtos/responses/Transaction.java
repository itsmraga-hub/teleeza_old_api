package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Transaction{

	@JsonProperty("reference")
	private String reference;

	@JsonProperty("date")
	private String date;

	@JsonProperty("note")
	private String note;

	@JsonProperty("country")
	private String country;

	@JsonProperty("amount")
	private String amount;

	@JsonProperty("code")
	private String code;

	@JsonProperty("currency")
	private String currency;

	@JsonProperty("source")
	private String source;

	@JsonProperty("type")
	private String type;

	@JsonProperty("status")
	private String status;

	@JsonProperty("network")
	private String network;

	public void setReference(String reference){
		this.reference = reference;
	}

	public String getReference(){
		return reference;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setNote(String note){
		this.note = note;
	}

	public String getNote(){
		return note;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setAmount(String amount){
		this.amount = amount;
	}

	public String getAmount(){
		return amount;
	}

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setSource(String source){
		this.source = source;
	}

	public String getSource(){
		return source;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setNetwork(String network){
		this.network = network;
	}

	public String getNetwork(){
		return network;
	}
}