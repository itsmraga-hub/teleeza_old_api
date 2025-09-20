package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mobile_money_transactions")
public class MobileMoneyTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String beneficiaryAccNumber;
    private String transactionReference;
    private String mobileOperatorNumber;
    private String merchantCode;
    private double amount;
    private String channelCode;
    private int transactionFee;
    private String reason;
    private String merchantRequestId;
    private String checkoutRequestId;
    private Integer resultCode;
    private String resultDescription;
    private String transactionAmount;
    private String merchantTransactionReference;
    private String recipientNumber;
    private String destinationChannel;
    private String sourceChannel;
    private String sasaPayTransId;
    private String recipientName;
    private String senderAccountNumber;
    private String transactionDate;
    private String  referenceNumber;
    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

}
