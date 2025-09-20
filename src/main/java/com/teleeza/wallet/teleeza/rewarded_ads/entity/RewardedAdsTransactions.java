package com.teleeza.wallet.teleeza.rewarded_ads.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name ="rewarded_ads_transactions")
@Entity
@Data
public class RewardedAdsTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String transactionReceipt;
    private String resultCode;
    private String transactionDesc;
    private String amount;
    private String transactionCompletionDate;
    private String resultDesc;
    private String originatorConversationId;
    private String conversationId;
    private String isCustomerRegistered;
    private Integer resultType;
    private String receiverPublicName;
    private Long advertId;

}
