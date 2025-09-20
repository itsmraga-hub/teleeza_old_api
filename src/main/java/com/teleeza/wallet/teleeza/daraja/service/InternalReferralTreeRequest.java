package com.teleeza.wallet.teleeza.daraja.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalReferralTreeRequest {
    @JsonProperty("referral_code")
    private String referralCode;
    @JsonProperty("plan")
    private String subscriptionPlan;
    @JsonProperty("is_initial")
    private Boolean isInitial;
//    @JsonProperty("referrals_count")
//    private String referrals_count;
//    @JsonProperty("traversal_direction")
//    private String traversal_direction;
}
