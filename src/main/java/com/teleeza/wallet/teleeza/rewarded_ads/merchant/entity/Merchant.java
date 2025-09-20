package com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "merchant_name")
    private String merchantName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "location")
    private String location;
    @Column(name = "company")
    private String company;
    @Column(name = "is_till")
    private Boolean isTill = false;
    @Column(name = "is_paybill")
    private Boolean isPayBill = false;
    @Column(name = "bill_number")
    private String billNumber;
    @Column(name = "account_no")
    private String accountNumber;
    @Column(name = "contact_person")
    private String contactPerson;
    @Column(name = "contact_person_phone_no")
    private String contactPersonPhoneNo;
    @Column(name = "kra_pin")
    private String kraPin;
    @Column(name = "kra_pdf_url")
    private String kraPdfUrl;
    @Column(name = "merchant_no")
    private String merchantNumber;
    @Column(name = "merchant_type")
    private String merchantType;
    
}
