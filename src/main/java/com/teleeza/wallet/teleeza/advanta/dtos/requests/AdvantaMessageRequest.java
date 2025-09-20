package com.teleeza.wallet.teleeza.advanta.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvantaMessageRequest {
    @JsonProperty("apikey")
    private String apiKey;
    @JsonProperty("partnerID")
    private String partnerID;
    @JsonProperty("message")
    private String message;
    @JsonProperty("shortcode")
    private String shortCode;
    @JsonProperty("mobile")
    private String mobile;

}
