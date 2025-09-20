package com.teleeza.wallet.teleeza.topups.send_airtime.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "airtime_ad_rewards")
public class AirtimeAdRewards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String note;
    private String amount;

    @Column(name = "adTitle")
    private String title;

    private String mobile;
    private String currency;

    private String network;
    private String status;
    private String code;
    private String reference;

    @Column(nullable = true)
    private String adType;

//    public AirtimeAdRewards() {
//    }

//    public AirtimeAdRewards(Long id, String country, String note, String amount,
//                            String mobile, String currency, String network,
//                            String status, String code, String reference, String adType) {
//        this.id = id;
//        this.country = country;
//        this.note = note;
//        this.amount = amount;
//        this.mobile = mobile;
//        this.currency = currency;
//        this.network = network;
//        this.status = status;
//        this.code = code;
//        this.reference = reference;
//        this.adType = adType;
//    }

//    public Long getId() {
//        return id;
//    }

//    public void setId(Long id) {
//        this.id = id;
//    }

//    public String getCountry() {
//        return country;
//    }

//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public String getNote() {
//        return note;
//    }
//
//    public void setNote(String note) {
//        this.note = note;
//    }
//
//    public String getAmount() {
//        return amount;
//    }
//
//    public void setAmount(String amount) {
//        this.amount = amount;
//    }

//    public String getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(String mobile) {
//        this.mobile = mobile;
//    }
//
//    public String getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(String currency) {
//        this.currency = currency;
//    }
//
//    public String getNetwork() {
//        return network;
//    }
//
//    public void setNetwork(String network) {
//        this.network = network;
//    }
//
//    public String getStatus() {
//        return status;
//    }

//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getReference() {
//        return reference;
//    }
//
//    public void setReference(String reference) {
//        this.reference = reference;
//    }

//    public String getAdType() {
//        return adType;
//    }
//
//    public void setAdType(String adType) {
//        this.adType = adType;
//    }
}
