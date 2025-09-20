package com.teleeza.wallet.teleeza.subscription.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
// contains all subscription attempts
@Entity
@Table(name = "users_subscriptions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_subscriptions_seq")
    @SequenceGenerator(name = "users_subscriptions_seq", sequenceName = "users_subscriptions_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "start_date", nullable = true)
    private LocalDateTime startTime;
    @Column(name = "expiration_date", nullable = true)
    private LocalDateTime expirationTime;
    private Boolean isSubscriptionSatus;
    @Column(name = "beneficiary_account_number", unique = true)
    private String beneficiaryAccountNumber;
    private String merchantReference;
    private String planId;
    private String planName;
    private Boolean referrerCredited = false;
    //    private Boolean isInitialSubscription ;
//    private Boolean isRenewal ;
    @Column(name = "amount")
    private Integer amount;
    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
