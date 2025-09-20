package com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "redeemed_vouchers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RedeemedVouchers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "code")
    private String code;
    @Column(name = "value")
    private BigDecimal value;
//    Merchant phone Number
    private String phoneNumber;
    private String adTitle;
    private String adType;
    @Column(name = "redeemed_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "EAT")
    private LocalDateTime redeemedAt;
    @Column(name = "result_code")
    private Long resultCode;
    @Column(name = "advert_id")
    private Long advertId;
//
    private String clientPhoneNumber;
}
