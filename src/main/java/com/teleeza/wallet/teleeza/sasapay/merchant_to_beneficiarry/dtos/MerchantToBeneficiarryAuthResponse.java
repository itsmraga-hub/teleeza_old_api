package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantToBeneficiarryAuthResponse{

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("scope")
	private String scope;

	@JsonProperty("detail")
	private String detail;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("expires_in")
	private int expiresIn;

	@JsonProperty("status")
	private boolean status;

	public String getAccessToken(){
		return accessToken;
	}

	public String getScope(){
		return scope;
	}

	public String getDetail(){
		return detail;
	}

	public String getTokenType(){
		return tokenType;
	}

	public int getExpiresIn(){
		return expiresIn;
	}

	public boolean isStatus(){
		return status;
	}
}