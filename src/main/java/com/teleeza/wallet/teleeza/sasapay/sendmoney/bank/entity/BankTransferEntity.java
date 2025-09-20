package com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banktransfer_transactions")
@Data
public class BankTransferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionReference;
    private String beneficiaryAccountNumber;
    private String receiverNumber;
    private String channelCode;
    private Integer amount;
    private String merchantCode;
    private Integer transactionFee;
    private String reason;
    private String referenceNumber;
    private String statusCode;
    private String merchantRequestId;
    private String checkoutRequestId;
    private String resultCode;
    private String resultDescription;
    private String transactionAmount;
    private String merchantTransactionReference;
    private String transactionDate;
    private String destinationChannel;
    private String sourceChannel;
    private String sasaPayTransId;
    private String recipientAccountNumber;
    private String recipientName;
    private String senderAccountNumber;
    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
