package com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
@lombok.Data
public class SendAirtimeResponse{

	@JsonProperty("data")
	private Data data;

	@JsonProperty("status")
	private Status status;




	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setStatus(Status status){
		this.status = status;
	}

	public Status getStatus(){
		return status;
	}
}