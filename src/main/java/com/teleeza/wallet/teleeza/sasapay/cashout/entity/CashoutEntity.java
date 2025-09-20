package com.teleeza.wallet.teleeza.sasapay.cashout.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cashout_transactions")
@Data
public class CashoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionReference;
    private String beneficiaryAccountNumber;
    private String sasaPayAgentNumber;
    private String amount;
    private String merchantCode;
    private String reason;
    private String merchantReference;
    private String statusCode;
    private String referenceNumber;

}
