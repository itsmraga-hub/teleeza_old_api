package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
@lombok.Data
public class Data{

	@JsonProperty("sender")
	private Sender sender;

	@JsonProperty("recipient")
	private Recipient recipient;

	@JsonProperty("channel")
	private Channel channel;

	@JsonProperty("errors")
	private List<Object> errors;

	@JsonProperty("transaction")
	private Transaction transaction;

	@JsonProperty("messaging")
	private Messaging messaging;

	public void setSender(Sender sender){
		this.sender = sender;
	}

	public Sender getSender(){
		return sender;
	}

	public void setRecipient(Recipient recipient){
		this.recipient = recipient;
	}

	public Recipient getRecipient(){
		return recipient;
	}

	public void setChannel(Channel channel){
		this.channel = channel;
	}

	public Channel getChannel(){
		return channel;
	}

	public void setErrors(List<Object> errors){
		this.errors = errors;
	}

	public List<Object> getErrors(){
		return errors;
	}

	public void setTransaction(Transaction transaction){
		this.transaction = transaction;
	}

	public Transaction getTransaction(){
		return transaction;
	}

	public void setMessaging(Messaging messaging){
		this.messaging = messaging;
	}

	public Messaging getMessaging(){
		return messaging;
	}
}