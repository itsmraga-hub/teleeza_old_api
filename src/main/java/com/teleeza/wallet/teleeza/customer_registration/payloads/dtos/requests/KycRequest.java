package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KycRequest{

	@JsonProperty("BeneficiaryAccountNumber")
	private String beneficiaryAccountNumber;

	@JsonProperty("DocumentImageBack")
	private byte[] documentImageBack;

	@JsonProperty("PassportSizePhoto")
	private byte[]  passportSizePhoto;

	@JsonProperty("MerchantCode")
	private String merchantCode;

	@JsonProperty("DocumentImageFront")
	private byte[]  documentImageFront;
}