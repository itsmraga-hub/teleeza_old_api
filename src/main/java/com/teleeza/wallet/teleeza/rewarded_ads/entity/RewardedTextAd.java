package com.teleeza.wallet.teleeza.rewarded_ads.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rewarded_text_ads")
@Data
@NoArgsConstructor
@Getter
@Setter

@SqlResultSetMapping(
        name = "filteredRewardedTextAdsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = TextAds.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "question", type = String.class),
                                @ColumnResult(name = "choicea", type = String.class),
                                @ColumnResult(name = "choiceb", type = String.class),
                                @ColumnResult(name = "choicec", type = String.class),
                                @ColumnResult(name = "answer", type = String.class),
                                @ColumnResult(name = "amount", type = String.class),
                                @ColumnResult(name = "target_amount", type = Integer.class),
                                @ColumnResult(name = "industry", type = String.class),
                                @ColumnResult(name = "company", type = String.class),
                                @ColumnResult(name = "duration", type = String.class),
                                @ColumnResult(name = "background_color", type = String.class),
                                @ColumnResult(name = "text_color", type = String.class),
                                @ColumnResult(name = "ad_type", type = String.class),
                                @ColumnResult(name = "brand_logo", type = String.class),
                                @ColumnResult(name = "call_to_action", type = String.class),
                                @ColumnResult(name = "call_to_action_text_color", type = String.class),
                                @ColumnResult(name = "call_to_action_background_color", type = String.class),
                                @ColumnResult(name = "call_to_action_url", type = String.class),
                                @ColumnResult(name = "call_to_action_contact", type = String.class),
                                @ColumnResult(name = "question_btn_background_color", type = String.class),
                                @ColumnResult(name = "question_btn_text_color", type = String.class),
                                @ColumnResult(name = "whatsapp_contact", type = String.class),
                                @ColumnResult(name = "opinion_question", type = String.class),
                                @ColumnResult(name = "isWatched", type = Boolean.class),
                                @ColumnResult(name = "is_approved", type = Boolean.class),
                                @ColumnResult(name = "is_closed", type = Boolean.class),
                                @ColumnResult(name = "total_views", type = Integer.class),
                                @ColumnResult(name = "created_by", type = String.class),
                                @ColumnResult(name = "reward_type", type = String.class),
                                @ColumnResult(name = "thumbnail", type = String.class),
                                @ColumnResult(name = "location", type = String.class),
                                @ColumnResult(name = "gender", type = String.class),
                                @ColumnResult(name = "age_group", type = String.class),
                                @ColumnResult(name = "target_group", type = Boolean.class),
                                @ColumnResult(name = "target_groups", type = String.class),
                        }
                )
        }
)
@NamedNativeQuery(
        name = "filteredTextAds",
        resultClass = TextAds.class,
        query = "SELECT\n" +
                "    rewarded_text_ads.id,\n" +
                "    rewarded_text_ads.title,\n" +
                "    rewarded_text_ads.description,\n" +
                "    rewarded_text_ads.image,\n" +
                "    rewarded_text_ads.question,\n" +
                "    rewarded_text_ads.choicea,\n" +
                "    rewarded_text_ads.choiceb,\n" +
                "    rewarded_text_ads.choicec,\n" +
                "    rewarded_text_ads.answer,\n" +
                "    rewarded_text_ads.amount,\n" +
                "    rewarded_text_ads.target_amount,\n" +
                "    rewarded_text_ads.industry,\n" +
                "    rewarded_text_ads.company,\n" +
                "    rewarded_text_ads.duration,\n" +
                "    rewarded_text_ads.background_color,\n" +
                "    rewarded_text_ads.text_color,\n" +
                "    rewarded_text_ads.ad_type,\n" +
                "    rewarded_text_ads.brand_logo,\n" +
                "    rewarded_text_ads.call_to_action,\n" +
                "    rewarded_text_ads.call_to_action_text_color,\n" +
                "    rewarded_text_ads.call_to_action_background_color,\n" +
                "    rewarded_text_ads.call_to_action_url,\n" +
                "    rewarded_text_ads.call_to_action_contact,\n" +
                "    rewarded_text_ads.question_btn_background_color,\n" +
                "    rewarded_text_ads.question_btn_text_color,\n" +
                "    rewarded_text_ads.opinion_question,\n" +
                "    rewarded_text_ads.whatsapp_contact,\n" +
                "    rewarded_text_ads.total_views,\n" +
                "    rewarded_text_ads.is_approved,\n" +
                "    rewarded_text_ads.is_closed,\n" +
                "    rewarded_text_ads.created_by,\n" +
                "    rewarded_text_ads.reward_type,\n" +
                "    rewarded_text_ads.thumbnail,\n" +
                "    rewarded_text_ads.location,\n" +
                "    rewarded_text_ads.gender,\n" +
                "    rewarded_text_ads.age_group,\n" +
                "    rewarded_text_ads.target_group,\n" +
                "    rewarded_text_ads.target_groups,\n" +
                "    (CASE\n" +
                "        WHEN rewarded_texts.count > 0 THEN 'true'\n" +
                "        ELSE 'false'\n" +
                "    END) AS isWatched\n" +
                "FROM rewarded_text_ads\n" +
                "LEFT JOIN (\n" +
                "    SELECT advert_id, COUNT(*) as count\n" +
                "    FROM rewarded_texts\n" +
                "    WHERE phone = :phone\n" +
                "    GROUP BY advert_id\n" +
                ") rewarded_texts\n" +
                "ON rewarded_text_ads.id = rewarded_texts.advert_id\n" +
                "WHERE rewarded_text_ads.end_date >= CURRENT_DATE()\n" +
                "AND rewarded_text_ads.start_date <= CURRENT_DATE()\n" +
                "AND rewarded_text_ads.industry = :industry\n"+
                "AND rewarded_text_ads.is_approved = true\n" +
//                "AND rewarded_text_ads.is_closed = false\n" +
                "ORDER BY rewarded_text_ads.created_at DESC, isWatched ASC"
        ,

        resultSetMapping = "filteredRewardedTextAdsMapping"
)

@SqlResultSetMapping(
        name = "rewardedTextAdsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = TextAds.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "question", type = String.class),
                                @ColumnResult(name = "choicea", type = String.class),
                                @ColumnResult(name = "choiceb", type = String.class),
                                @ColumnResult(name = "choicec", type = String.class),
                                @ColumnResult(name = "answer", type = String.class),
                                @ColumnResult(name = "amount", type = String.class),
                                @ColumnResult(name = "target_amount", type = Integer.class),
                                @ColumnResult(name = "industry", type = String.class),
                                @ColumnResult(name = "company", type = String.class),
                                @ColumnResult(name = "duration", type = String.class),
                                @ColumnResult(name = "background_color", type = String.class),
                                @ColumnResult(name = "text_color", type = String.class),
                                @ColumnResult(name = "ad_type", type = String.class),
                                @ColumnResult(name = "brand_logo", type = String.class),
                                @ColumnResult(name = "call_to_action", type = String.class),
                                @ColumnResult(name = "call_to_action_text_color", type = String.class),
                                @ColumnResult(name = "call_to_action_background_color", type = String.class),
                                @ColumnResult(name = "call_to_action_url", type = String.class),
                                @ColumnResult(name = "call_to_action_contact", type = String.class),
                                @ColumnResult(name = "question_btn_background_color", type = String.class),
                                @ColumnResult(name = "question_btn_text_color", type = String.class),
                                @ColumnResult(name = "whatsapp_contact", type = String.class),
                                @ColumnResult(name = "opinion_question", type = String.class),
                                @ColumnResult(name = "isWatched", type = Boolean.class),
                                @ColumnResult(name = "is_approved", type = Boolean.class),
                                @ColumnResult(name = "is_closed", type = Boolean.class),
                                @ColumnResult(name = "total_views", type = Integer.class),
                                @ColumnResult(name = "created_by", type = String.class),
                                @ColumnResult(name = "reward_type", type = String.class),
                                @ColumnResult(name = "thumbnail", type = String.class),
                                @ColumnResult(name = "location", type = String.class),
                                @ColumnResult(name = "gender", type = String.class),
                                @ColumnResult(name = "age_group", type = String.class),
                                @ColumnResult(name = "target_group", type = Boolean.class),
                                @ColumnResult(name = "target_groups", type = String.class),
                        }
                )
        }
)
@NamedNativeQuery(
        name = "rewardedTextAds",
        resultClass = TextAds.class,
        query = "SELECT\n" +
                "    rewarded_text_ads.id,\n" +
                "    rewarded_text_ads.title,\n" +
                "    rewarded_text_ads.description,\n" +
                "    rewarded_text_ads.image,\n" +
                "    rewarded_text_ads.question,\n" +
                "    rewarded_text_ads.choicea,\n" +
                "    rewarded_text_ads.choiceb,\n" +
                "    rewarded_text_ads.choicec,\n" +
                "    rewarded_text_ads.answer,\n" +
                "    rewarded_text_ads.location,\n" +
                "    rewarded_text_ads.amount,\n" +
                "    rewarded_text_ads.target_amount,\n" +
                "    rewarded_text_ads.industry,\n" +
                "    rewarded_text_ads.company,\n" +
                "    rewarded_text_ads.duration,\n" +
                "    rewarded_text_ads.background_color,\n" +
                "    rewarded_text_ads.text_color,\n" +
                "    rewarded_text_ads.ad_type,\n" +
                "    rewarded_text_ads.brand_logo,\n" +
                "    rewarded_text_ads.call_to_action,\n" +
                "    rewarded_text_ads.call_to_action_text_color,\n" +
                "    rewarded_text_ads.call_to_action_background_color,\n" +
                "    rewarded_text_ads.call_to_action_url,\n" +
                "    rewarded_text_ads.call_to_action_contact,\n" +
                "    rewarded_text_ads.question_btn_background_color,\n" +
                "    rewarded_text_ads.question_btn_text_color,\n" +
                "    rewarded_text_ads.whatsapp_contact,\n" +
                "    rewarded_text_ads.opinion_question,\n" +
                "    rewarded_text_ads.total_views,\n" +
                "    rewarded_text_ads.is_approved,\n" +
                "    rewarded_text_ads.is_closed,\n" +
                "    rewarded_text_ads.created_by,\n" +
                "    rewarded_text_ads.reward_type,\n" +
                "    rewarded_text_ads.thumbnail,\n" +
                "    rewarded_text_ads.gender,\n" +
                "    rewarded_text_ads.age_group,\n" +
                "    rewarded_text_ads.target_group,\n" +
                "    rewarded_text_ads.target_groups,\n" +
                "    (CASE\n" +
                "        WHEN rewarded_texts.count > 0 THEN 'true'\n" +
                "        ELSE 'false'\n" +
                "    END) AS isWatched\n" +
                "FROM rewarded_text_ads\n" +
                "LEFT JOIN (\n" +
                "    SELECT advert_id, COUNT(*) as count\n" +
                "    FROM rewarded_texts\n" +
                "    WHERE phone = ?1\n" +
                "    GROUP BY advert_id\n" +
                ") rewarded_texts\n" +
                "ON rewarded_text_ads.id = rewarded_texts.advert_id\n" +
                "WHERE rewarded_text_ads.end_date >= CURRENT_DATE()\n" +
                "AND rewarded_text_ads.start_date <= CURRENT_DATE()\n" +
                "AND rewarded_text_ads.is_approved = true\n" +
//                "AND rewarded_text_ads.is_closed = false\n" +
                "ORDER BY rewarded_text_ads.created_at DESC, isWatched ASC",

        resultSetMapping = "rewardedTextAdsMapping"
)
public class RewardedTextAd implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "image")
    private String image;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "industry")
    private String industry;

    @Column(name = "company")
    private String company;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @Column(name = "question")
    @Size(max = 200)
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String answer;
    private String amount;
    @Column(name = "target_amount")
    private Integer targetAmount;
    @Column(name = "background_color")
    private String backgroundColor;
    @Column(name = "text_color")
    private String textColor;
    private String adType;
    private String duration;
    private String location;
    @Column(name = "brand_logo")
    private String brandLogo;

    @Column(name = "call_to_action")
    private String callToAction;
    @Column(name = "call_to_action_text_color")
    private String callToActionTextColor;
    @Column(name = "call_to_action_background_color")
    private String callToActionBackgroundColor;

    @Column(name = "call_to_action_url")
    private String callToActionUrl;
    @Column(name = "call_to_action_contact")
    private String callToActionContact;
    @Column(name = "question_btn_background_color")
    private String questionBtnBackgroundColor;
    @Column(name = "question_btn_text_color")
    private String questionButtonTextColor;
    @Column(name = "whatsapp_contact")
    private String whatsAppContact;
    @Column(name = "opinion_question")
    private String opinionQuestion;

    @Column(name = "total_views")
    private Integer totalViews = 0;

    @Column(name = "is_approved")
    private Boolean isApproved = true;

    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @Column(name = "is_closed")
    private Boolean isClosed = false;
    @Column(name = "reward_type")
    private String rewardType;
    @Column(name = "brand")
    private String brand;
    @Column(name = "ad_budget")
    private String adBudget; // total budget of the ad before agency and Teleeza commissions are deducted
    @Column(name = "amount_paid_out")
    private Long amountPaidOut;// Amount paid out so far for watching ads

    @Column(name = "voucher_validity_end_date")
    private LocalDateTime voucherValidityEndDate;

    private String gender;
    @Column(name = "age_group")
    private String ageGroup;
    @Column(name = "target_group")
    private boolean targetGroup;
    @Column(name = "target_groups")
    private String targetGroups;
    public RewardedTextAd(Long id, String title, String description, String image, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
