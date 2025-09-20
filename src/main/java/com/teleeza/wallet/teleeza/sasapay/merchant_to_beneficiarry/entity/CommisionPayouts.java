package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name = "referral_commission_transactions")
@Data
@Entity
public class CommisionPayouts {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "referral_commission_transactions_seq")
    @SequenceGenerator(name = "referral_commission_transactions_seq", sequenceName = "referral_commission_id_seq", allocationSize = 1)
    private Long id;
    private String transactionReference;
    private String beneficiarryAccNumber;
    private Double amount;
    private String senderMerchantCode;
    private String receiverMerchantCode;
    private String reason;
    private Boolean status;
    private String message;
    private String merchantRequestId;
    private String checkoutRequestId;
}
