package com.teleeza.wallet.teleeza.daraja.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "commissions")
public class Commissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String agentAmount;
    private String superAgentAmount;
    private String coachAmount;
}
