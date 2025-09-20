package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Messaging{

	@JsonProperty("sender")
	private String sender;

	@JsonProperty("recipient")
	private String recipient;

	@JsonProperty("message")
	private String message;

	public void setSender(String sender){
		this.sender = sender;
	}

	public String getSender(){
		return sender;
	}

	public void setRecipient(String recipient){
		this.recipient = recipient;
	}

	public String getRecipient(){
		return recipient;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}