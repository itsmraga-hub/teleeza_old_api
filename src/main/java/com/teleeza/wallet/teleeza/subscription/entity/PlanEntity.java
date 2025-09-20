package com.teleeza.wallet.teleeza.subscription.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "subscription_plans")
public class PlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plans_seq")
    @SequenceGenerator(name = "plans_seq", sequenceName = "plans_id_seq", allocationSize = 1)
    private Long id;
    private String name;
    private Float price;
    private String planId;
    private Float additionalMemberPrice;
}
