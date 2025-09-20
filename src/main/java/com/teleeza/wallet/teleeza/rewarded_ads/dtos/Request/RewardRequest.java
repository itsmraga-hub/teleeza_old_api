package com.teleeza.wallet.teleeza.rewarded_ads.dtos.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RewardRequest {
    private String phoneNumber;
    private String advertId;
    private BigDecimal amount;
    private String adType;
    private String opinionAnswer;
    private String accountNumber;
    private String gender;
    private String location;
    private String age;
    private String rewardType;
    private Integer isSponsored = 0;
}
