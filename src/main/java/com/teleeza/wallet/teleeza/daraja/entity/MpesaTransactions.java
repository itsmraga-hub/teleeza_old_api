package com.teleeza.wallet.teleeza.daraja.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mpesa_transactions")
public class MpesaTransactions  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionType;
    private String amount;
    private String phoneNumber;
    private String partyA;
    private String partyB;
    private String accountReference;
    private String transactionDesc;
    private String businessShortCode;
    private String timeStamp;
    private String merchantRequestId;
    private String checkoutRequestId;
    private String responseCode;
    private String responseDescription;
    private String conversationID;
    private String originatorConversationID;
    private Integer resultCode;
    private String resultDesc;
    private String mpesaReceiptNumber;
    private String transactionDate;
    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @Column(name = "utility_balance")
    private String utilityBalance;
    private String subscriptionPlan;
    private Boolean paid = false;
}
