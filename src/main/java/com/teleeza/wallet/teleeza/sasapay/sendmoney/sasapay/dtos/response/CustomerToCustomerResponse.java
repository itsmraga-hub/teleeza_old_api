package com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerToCustomerResponse{

	@JsonProperty("MerchantReference")
	private String merchantReference;

	@JsonProperty("TransactionReference")
	private String transactionReference;

//	@JsonProperty("TransactionCost")
//	private int transactionCost;

	@JsonProperty("message")
	private String message;

	@JsonProperty("statusCode")
	private String statusCode;


}