package com.teleeza.wallet.teleeza.rewarded_ads.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_voucher_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "voucher_code")
    private String voucherCode;
    @Column(name = "advert_id")
    private int advertId;
    @Column(name = "advert_type")
    private String advertType;
    @Column(name = "is_redeemed")
    private boolean redeemed;
    @Column(name = "reusable")
    private boolean reusable;
    @Column(name = "date_sent_out")
    private LocalDateTime dateSentOut;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "client_phone_number")
    private String clientPhoneNumber;
    @Column(name = "client_name")
    private String clientName;
    @Column(name = "client_gender")
    private String clientGender;
    @Column(name = "client_location")
    private String clientLocation;
}