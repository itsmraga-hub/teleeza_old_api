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

@Entity
@Table(name = "subscriptions")
@AllArgsConstructor
@NoArgsConstructor
@Data
//@SqlResultSetMapping(
//        name = "topEarnersMapping",
//        classes = {
//                @ConstructorResult(
//                        targetClass = TopPerfomers.class,
//                        columns = {
//                                @ColumnResult(name = "displayName", type = String.class),
//                                @ColumnResult(name = "Commission",type = String.class),
//                                @ColumnResult(name = "referralsCount",type = String.class),
//                                @ColumnResult(name = "photoUrl", type = String.class),
//                        }
//                )
//        }
//)
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscriptions_seq")
    @SequenceGenerator(name = "subscriptions_seq", sequenceName = "subscriptions_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "start_date", nullable = true)
    private LocalDateTime startTime;
    @Column(name = "expiration_date", nullable = true)
    private LocalDateTime expirationTime;
    private Boolean isSubscriptionSatus;
    @Column(name = "beneficiary_account_number")
    private String beneficiaryAccountNumber;
    private String merchantReference;
    private String planId;
    private String planName;
    private Boolean isInitialSubscription;
    private Boolean credited;
    private Boolean picked;
    private String referredByCode;
    @Column(name = "amount")
    private String amount;
    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
