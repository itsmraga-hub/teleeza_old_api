package com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "customer_to_customer_transactions")
@Entity
@Data
public class CustomerToCustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionReference;
    private String senderBeneficiaryAccountNumber;
    private String recipientBeneficiaryAccountNumber;
    private Integer amount;
    private String merchantCode;
    private Integer transactionFee;
    private String statusCode;
    private String merchantReference;
    private String merchantRequestId;
    private String checkoutRequestId;
    private Integer resultCode;
    private String resultDescription;
    private String transactionAmount;
    private String billRefNumber;
    private String transactionDate;
    private String senderCustomerMobile;
    private String receiverCustomerMobile;
    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
