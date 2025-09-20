package com.teleeza.wallet.teleeza.customer_registration.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TopPerfomers;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Entity(name = "users")
@Table
//@AllArgsConstructor
//@NoArgsConstructor
@SqlResultSetMapping(
        name = "topEarnersMapping",
        classes = {
                @ConstructorResult(
                        targetClass = TopPerfomers.class,
                        columns = {
                                @ColumnResult(name = "firstName", type = String.class),
                                @ColumnResult(name = "surName", type = String.class),
//                                @ColumnResult(name = "Commission",type = String.class),
                                @ColumnResult(name = "referrals", type = String.class),
                                @ColumnResult(name = "photoUrl", type = String.class),
                        }
                )
        }
)
@NamedNativeQuery(
        name = "topEarners",
        resultClass = TopPerfomers.class,
//        query = "select u1.first_name as firstName , u1.surname as surName, u1.photo_url as photoUrl, count(u2.referredby_code) as referrals ,  (select count(tc.referredby_code) from users tc where tc.created_on = curdate()) as todaysReferrals from users u1 left join users u2 on u1.referral_code = u2.referredby_code  where  u2.referredby_code is not null and u1.is_staff is false or u1.is_staff is null group by u1.account_number order by referrals desc;\n",
        query = "select u1.first_name as firstName , u1.surname as surName, u1.photo_url as photoUrl, count(u2.referredby_code) as referrals ,  (select count(u1.created_on) from users u1 where date (`created_on`) = current_date()) as todaysCount from users u1 left join users u2 on u1.referral_code = u2.referredby_code  where  u2.referredby_code is not null and u1.is_staff is false or u1.is_staff is null group by u1.account_number order by referrals desc limit 15;\n",
//        query = "select u1.first_name as firstName , u1.surname as surName, u1.photo_url as photoUrl, count(u2.referredby_code) as referrals  from users u1 left join users u2 on u1.referral_code = u2.referredby_code where u2.referredby_code is not null group by u1.account_number order by referrals desc ;\n",
//        query = "select c.display_name as displayName,c.photo_url as photoUrl, sum(v.transaction_amount)" +
//                " as Commission, count(sender_account_number) as referralsCount from users c \n" +
//                "left join validated_transactions v " +
//                "on c.account_number = v.sender_account_number where reason='Referral Commission' or reason = 'Residual Commission'\n" +
//                "GROUP BY c.account_number order by Commission desc",
        resultSetMapping = "topEarnersMapping"
)


public class CustomerEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantCode;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "surname")
    private String lastName;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "phone", unique = true)
    private String mobileNumber;
    private String documentType;
    @Column(name = "document_number")
    private String documentNumber;
    private String email;
    private String statusCode;
    private String otp;
    @Column(name = "otp_attempts")
    private Integer otpAttempts = 0;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "EAT")
    @Column(name = "otp_expiry_time")
    private LocalDateTime otpExpiryTime;
    @Column(name = "account_number")
    private String customerAccountNumber;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "aml_score")
    private Integer amlScore;
    @Column(name = "redeemed_points")
    private Integer redeemedPoints;
    @Column(name = "dob")
    private String dob;
    @Column(name = "age_group_no")
    private Integer ageGroupNo;
    @Column(name = "gender")
    private String gender;
    @Column(name = "age")
    private Integer age;
    //        @Column(name = "invitation_code")
    @Column(name = "referral_code")
    private String referralCode;
    @Column(name = "referredby_code", nullable = true)
//    @Column(name = "invited_by",nullable = true)
    private String referredByCode;
    @Column(name = "location")
    private String location;
    @Column(name = "is_policy_accepted")
    private Boolean isPolicyAccepted = false;
    private Boolean isSubscribed = false;
    private Boolean isInitialSubscription = true;
    private Boolean isRenewal = false;
    private Boolean isKycSubmitted = false;
    @Column(name = "fcm_token")
    private String fcmToken;
    @Column(name = "auth_provider")
    private String authProvider = "token";
    @Column(name = "password")
    private String password = "$2a$10$7g.TAJrN0SH.gXxFS6AL2uukYw57N8fW82CU1IbvqJJKNszglvRpi";
    @Column(name = "user_type_no")
    private Integer userType = 4;
    @Column(name = "photo_url")
    private String photoUrl;
    @Column(name = "id_front")
    private String idFront;
    @Column(name = "id_back")
    private String idBack;
    @Column(name = "mobile_user_type")
    private String mobileUserType = "Agent";
    @Column(name = "expiration_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "EAT")
    private LocalDateTime expirationTime;
    private String organisation;
    private Boolean isStaff = false;
    @Column(name = "wallet_status")
    private Integer walletStatus;
    @Column(name = "updated_on")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "EAT")
    private LocalDateTime updatedOn;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "accident_cover")
    private Boolean accidentCover = false;

    @Column(name = "mobile_verified")
    private Boolean mobileVerified = false;

    @Column(name = "locked")
    private Boolean locked = false;

    @Column(name = "message_id")
    private String messageId;
    @Column(name = "is_referrer_paid")
    private Boolean isReferrerPaid = false;



    public CustomerEntity() {
    }

    public CustomerEntity(Long id,
                          String merchantCode,
                          String firstName,
                          String middleName,
                          String lastName,
                          String countryCode,
                          String mobileNumber,
                          String documentType,
                          String documentNumber,
                          String email,
                          String statusCode,
                          String customerAccountNumber,
                          String displayName,
                          Integer amlScore,
                          Integer redeemedPoints

    ) {
        this.id = id;
        this.merchantCode = merchantCode;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.countryCode = countryCode;
        this.mobileNumber = mobileNumber;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.email = email;
        this.statusCode = statusCode;
        this.customerAccountNumber = customerAccountNumber;
        this.displayName = displayName;
        this.amlScore = amlScore;
        this.redeemedPoints = redeemedPoints;
    }

}
