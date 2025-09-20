package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "top_earners")
public class TopEarners {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "top_earners_seq")
    @SequenceGenerator(name = "top_earners_seq",sequenceName = "top_earners_seq_id_seq",allocationSize = 1)
    private Long id;
    private String displayName;
    private String account_number;
    private String amount;
}
