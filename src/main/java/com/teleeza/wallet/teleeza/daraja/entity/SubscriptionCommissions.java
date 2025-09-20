package com.teleeza.wallet.teleeza.daraja.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "subscription_commissions")
@Entity
public class SubscriptionCommissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String agentAmount;
    private String superAgentAmount;
    private String coachAmount;
}
