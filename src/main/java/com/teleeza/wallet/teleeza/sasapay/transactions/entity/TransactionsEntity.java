package com.teleeza.wallet.teleeza.sasapay.transactions.entity;

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
import java.util.Date;
// This table collects all transaction attempts
@Table(name = "all_transactions")
@Entity
@Data
public class TransactionsEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String merchantRequestId; // merchanttransaction reference
    private String transactionReference;
    private String checkoutRequestId;
    private String beneficiaryAccNumber;
    private String senderBeneficiaryAccNumber;
    private String recipientBeneficiaryAccNumber;
    private String resultCode; // also status code
    private String reason;
    private String resultDesc; // also message for utilities and airtimes
    private Double transAmount;// transaction amount after validation from sasapay
    private Double amount;
    private String billRefNo;
    private String transactionDate; // also transtime for utilities
    private String customerMobile;
    private String thirdPartyTransId;
    private String networkCode; //also channel code
    private String senderAccountNumber;  // for tv,airtime,pay bills, tills
    private String accountNumber; // accs being credited airtime,water,paybills, tv
    private String merchantCode;
    private String serviceCode; // utilities
    private String sasaPayTransId; // transId
    private String recipientAccountNumber;
    private String recipientName;
    private String sourceChannel;
    private String destinationChannel;
    private String merchantTransactionRef;
    private String senderCustomerMobile;
    private String receiverCustomerMobile;
    private Boolean isTransactionType; // true for money in
    private String CommissionTotal;
    private Integer transactionFee;
    private String manualPayment;
    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

}
