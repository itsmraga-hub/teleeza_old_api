package com.teleeza.wallet.teleeza.sasapay.transactions.entity;

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
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Table
@Entity(name = "validated_transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
//@NamedNativeQuery(
//        name = "topEarners",
//        resultClass = TopPerfomers.class,
//        query = "select c.display_name as displayName,c.photo_url as photoUrl, sum(v.transaction_amount)" +
//                " as Commission, count(sender_account_number) as referralsCount from users c \n" +
//                "left join validated_transactions v " +
//                "on c.account_number = v.sender_account_number where reason='Referral Commission' or reason = 'Residual Commission'\n" +
//                "GROUP BY c.account_number order by Commission desc",
//        resultSetMapping = "topEarnersMapping"
//)
public class Transactions implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_seq")
    @SequenceGenerator(name = "transactions_seq", sequenceName = "transactions_id_seq", allocationSize = 1)
    private Long id;
    private String merchantRequestId;
    private String merchantTransactionReference;
    private String sourceChannel;
    private String destinationChannel;
    private String recipientAccountNumber;
    private String beneficiaryAccNumber;
    private String transactionAmount;
    private String senderAccountNumber;
    private String merchantAccountBalance;
    private String merchantCode;
    private String checkoutRequestId;
    private String recipientName;
    private String senderName;
    private String resultDesc;
    private String sasaPayTransactionId;
    private String billRefNumber;
    private String thirdPartyId;
    private String voucherType;
    private String serviceCode;
    private String statusCode;
    private String pin;
    private String units;
    private String reason;
    private int resultCode;
    private String transactionDate;
    private String transactionFee;
    private String senderMerchantCode;
    private Boolean isTransactionType = false; // true for money in
    private String customerBalance;
    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
