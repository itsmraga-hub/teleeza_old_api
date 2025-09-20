package com.teleeza.wallet.teleeza.subscription.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name = "organisations")
@Entity
@Data
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String organisationName;
    private String organisationCode;
    private String contact;

}
