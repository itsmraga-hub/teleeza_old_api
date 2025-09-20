package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRegistrationAsyncCallBackRequest{
	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;
	@JsonProperty("MerchantCode")
	private String merchantCode;
	@JsonProperty("MessageDescription")
	private String messageDescription;
	@JsonProperty("BeneficiaryStatus")
	private Integer beneficiaryStatus;
}