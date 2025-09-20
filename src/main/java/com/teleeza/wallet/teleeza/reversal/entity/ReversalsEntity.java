package com.teleeza.wallet.teleeza.reversal.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name = "reversal_transactions")
@Entity
@Data
public class ReversalsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String receiptNo;
    private String oppositeParty;
    private Double amount;
    private String accountNumber;
    private String merchantCode;
    private String reason;
    private String transactionReference;
    private String statusCode;
    private String merchantReference;
    private String referenceNumber;
    private String senderAccountNumber;
}
