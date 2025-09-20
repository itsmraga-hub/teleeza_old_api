package com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KplcAsyncRequest{

	@JsonProperty("ServiceCode")
	private String serviceCode;

	@JsonProperty("SenderAccountNumber")
	private String senderAccountNumber;

	@JsonProperty("Pin")
	private String pin;

	@JsonProperty("Message")
	private String message;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("Amount")
	private double amount;

	@JsonProperty("StatusCode")
	private String statusCode;

	@JsonProperty("NetworkCode")
	private String networkCode;

	@JsonProperty("TransTime")
	private String transTime;

	@JsonProperty("Units")
	private String units;

	@JsonProperty("VoucherType")
	private String voucherType;

	@JsonProperty("AccountNumber")
	private String accountNumber;
}