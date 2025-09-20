package com.teleeza.wallet.teleeza.rewarded_ads.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "rewarded_audio_ads")
@Data
@NoArgsConstructor
@Getter
@Setter

@SqlResultSetMapping(
        name = "filteredRewardedAudioAdsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = AudioAds.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "file_path", type = String.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "audio_image_path", type = String.class),
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
                                @ColumnResult(name = "brand_logo", type = String.class),
                                @ColumnResult(name = "call_to_action", type = String.class),
                                @ColumnResult(name = "call_to_action_text_color", type = String.class),
                                @ColumnResult(name = "call_to_action_background_color", type = String.class),
                                @ColumnResult(name = "call_to_action_url", type = String.class),
                                @ColumnResult(name = "call_to_action_contact", type = String.class),
                                @ColumnResult(name = "opinion_question", type = String.class),
                                @ColumnResult(name = "whatsapp_contact", type = String.class),
                                @ColumnResult(name = "isWatched", type = Boolean.class),
                                @ColumnResult(name = "is_approved", type = Boolean.class),
                                @ColumnResult(name = "is_closed", type = Boolean.class),
                                @ColumnResult(name = "total_views", type = Integer.class),
                                @ColumnResult(name = "created_by", type = String.class),
                                @ColumnResult(name = "reward_type", type = String.class),
                                @ColumnResult(name = "location", type = String.class),
                                @ColumnResult(name = "gender", type = String.class),
                                @ColumnResult(name = "age_group", type = String.class),
                                @ColumnResult(name = "target_group", type = Boolean.class),
                                @ColumnResult(name = "target_groups", type = String.class),
                                @ColumnResult(name = "reward_text", type = String.class),
                        }
                )
        }
)
@NamedNativeQuery(
        name = "filteredRewardedAudioAds",
        resultClass = AudioAds.class,
        query = "SELECT\n" +
                "    rewarded_audio_ads.id,\n" +
                "    rewarded_audio_ads.title,\n" +
                "    rewarded_audio_ads.description,\n" +
                "    rewarded_audio_ads.file_path,\n" +
                "    rewarded_audio_ads.image,\n" +
                "    rewarded_audio_ads.audio_image_path,\n" +
                "    rewarded_audio_ads.question,\n" +
                "    rewarded_audio_ads.choicea,\n" +
                "    rewarded_audio_ads.choiceb,\n" +
                "    rewarded_audio_ads.choicec,\n" +
                "    rewarded_audio_ads.answer,\n" +
                "    rewarded_audio_ads.amount,\n" +
                "    rewarded_audio_ads.target_amount,\n" +
                "    rewarded_audio_ads.industry,\n" +
                "    rewarded_audio_ads.company,\n" +
                "    rewarded_audio_ads.duration,\n" +
                "    rewarded_audio_ads.brand_logo,\n" +
                "    rewarded_audio_ads.call_to_action,\n" +
                "    rewarded_audio_ads.call_to_action_text_color,\n" +
                "    rewarded_audio_ads.call_to_action_background_color,\n" +
                "    rewarded_audio_ads.call_to_action_url,\n" +
                "    rewarded_audio_ads.call_to_action_contact,\n" +
                "    rewarded_audio_ads.opinion_question,\n" +
                "    rewarded_audio_ads.whatsapp_contact,\n" +
                "    rewarded_audio_ads.total_views,\n" +
                "    rewarded_audio_ads.is_approved,\n" +
                "    rewarded_audio_ads.is_closed,\n" +
                "    rewarded_audio_ads.created_by,\n" +
                "    rewarded_audio_ads.reward_type,\n" +
                "    rewarded_audio_ads.location,\n" +
                "    rewarded_audio_ads.gender,\n" +
                "    rewarded_audio_ads.age_group,\n" +
                "    rewarded_audio_ads.target_group,\n" +
                "    rewarded_audio_ads.target_groups,\n" +
                "    rewarded_audio_ads.reward_text,\n" +
                "    (CASE\n" +
                "        WHEN rewarded_audio.count > 0 THEN 'true'\n" +
                "        ELSE 'false'\n" +
                "    END) AS isWatched\n" +
                "FROM rewarded_audio_ads\n" +
                "LEFT JOIN (\n" +
                "    SELECT advert_id, COUNT(*) as count\n" +
                "    FROM rewarded_audio\n" +
                "    WHERE phone = :phone\n" +
                "    GROUP BY advert_id\n" +
                ") rewarded_audio\n" +
                "ON rewarded_audio_ads.id = rewarded_audio.advert_id\n" +
                "WHERE rewarded_audio_ads.end_date >= CURRENT_DATE()\n" +
                "AND rewarded_audio_ads.start_date <= CURRENT_DATE()\n" +
                "AND rewarded_audio_ads.industry = :industry\n" +
                "AND rewarded_audio_ads.is_approved = true\n" +
//                "AND rewarded_audio_ads.is_closed = false\n" +
                "ORDER BY rewarded_audio_ads.created_at DESC, isWatched ASC",

        resultSetMapping = "filteredRewardedAudioAdsMapping"
)


@SqlResultSetMapping(
        name = "rewardedAudioAdsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = AudioAds.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "file_path", type = String.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "audio_image_path", type = String.class),
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
                                @ColumnResult(name = "brand_logo", type = String.class),
                                @ColumnResult(name = "call_to_action", type = String.class),
                                @ColumnResult(name = "call_to_action_text_color", type = String.class),
                                @ColumnResult(name = "call_to_action_background_color", type = String.class),
                                @ColumnResult(name = "call_to_action_url", type = String.class),
                                @ColumnResult(name = "call_to_action_contact", type = String.class),
                                @ColumnResult(name = "opinion_question", type = String.class),
                                @ColumnResult(name = "whatsapp_contact", type = String.class),
                                @ColumnResult(name = "isWatched", type = Boolean.class),
                                @ColumnResult(name = "is_approved", type = Boolean.class),
                                @ColumnResult(name = "is_closed", type = Boolean.class),
                                @ColumnResult(name = "total_views", type = Integer.class),
                                @ColumnResult(name = "created_by", type = String.class),
                                @ColumnResult(name = "reward_type", type = String.class),
                                @ColumnResult(name = "location", type = String.class),
                                @ColumnResult(name = "gender", type = String.class),
                                @ColumnResult(name = "age_group", type = String.class),
                                @ColumnResult(name = "target_group", type = Boolean.class),
                                @ColumnResult(name = "target_groups", type = String.class),
                                @ColumnResult(name = "reward_text", type = String.class),
                        }
                )
        }
)
@NamedNativeQuery(
        name = "rewardedAudiosAds",
        resultClass = AudioAds.class,
        query = "SELECT\n" +
                "    rewarded_audio_ads.id,\n" +
                "    rewarded_audio_ads.title,\n" +
                "    rewarded_audio_ads.description,\n" +
                "    rewarded_audio_ads.file_path,\n" +
                "    rewarded_audio_ads.image,\n" +
                "    rewarded_audio_ads.audio_image_path,\n" +
                "    rewarded_audio_ads.question,\n" +
                "    rewarded_audio_ads.choicea,\n" +
                "    rewarded_audio_ads.choiceb,\n" +
                "    rewarded_audio_ads.choicec,\n" +
                "    rewarded_audio_ads.answer,\n" +
                "    rewarded_audio_ads.amount,\n" +
                "    rewarded_audio_ads.target_amount,\n" +
                "    rewarded_audio_ads.industry,\n" +
                "    rewarded_audio_ads.company,\n" +
                "    rewarded_audio_ads.duration,\n" +
                "    rewarded_audio_ads.brand_logo,\n" +
                "    rewarded_audio_ads.call_to_action,\n" +
                "    rewarded_audio_ads.call_to_action_text_color,\n" +
                "    rewarded_audio_ads.call_to_action_background_color,\n" +
                "    rewarded_audio_ads.call_to_action_url,\n" +
                "    rewarded_audio_ads.call_to_action_contact,\n" +
                "    rewarded_audio_ads.opinion_question,\n" +
                "    rewarded_audio_ads.whatsapp_contact,\n" +
                "    rewarded_audio_ads.total_views,\n" +
                "    rewarded_audio_ads.is_approved,\n" +
                "    rewarded_audio_ads.is_closed,\n" +
                "    rewarded_audio_ads.created_by,\n" +
                "    rewarded_audio_ads.reward_type,\n" +
                "    rewarded_audio_ads.location,\n" +
                "    rewarded_audio_ads.gender,\n" +
                "    rewarded_audio_ads.age_group,\n" +
                "    rewarded_audio_ads.target_group,\n" +
                "    rewarded_audio_ads.target_groups,\n" +
                "    rewarded_audio_ads.reward_text,\n" +
                "    (CASE\n" +
                "        WHEN rewarded_audio.count > 0 THEN 'true'\n" +
                "        ELSE 'false'\n" +
                "    END) AS isWatched\n" +
                "FROM rewarded_audio_ads\n" +
                "LEFT JOIN (\n" +
                "    SELECT advert_id, COUNT(*) as count\n" +
                "    FROM rewarded_audio\n" +
                "    WHERE phone = ?1\n" +
                "    GROUP BY advert_id\n" +
                ") rewarded_audio\n" +
                "ON rewarded_audio_ads.id = rewarded_audio.advert_id\n" +
                "WHERE rewarded_audio_ads.end_date >= CURRENT_DATE()" +
                "AND rewarded_audio_ads.start_date <= CURRENT_DATE()\n" +
                "AND rewarded_audio_ads.is_approved = true\n" +
//                "AND rewarded_audio_ads.is_closed = false\n" +
                "ORDER BY rewarded_audio_ads.created_at DESC, isWatched ASC",

        resultSetMapping = "rewardedAudioAdsMapping"
)
public class RewardedAudioAds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "file_path")
    private String filePath;
    @Column(name = "image")
    private String image;

    @Column(name = "audio_image_path")
    private String audioImagePath;

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
    private String duration;
    @Column(name = "industry")
    private String industry;

    @Column(name = "company")
    private String company;
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
    @Column(name = "opinion_question")
    private String opinionQuestion;
    @Column(name = "whatsapp_contact")
    private String whatsAppContact;
    @Column(name = "is_watched")
    private Boolean isWatched;

    @Column(name = "total_views")
    private Integer totalViews = 0;

    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "is_approved")
    private Boolean isApproved = true;

    @Column(name = "is_closed")
    private Boolean isClosed = false;

    @Column(name = "reward_type")
    private String rewardType;

    @Column(name = "brand")
    private String brand;
    @Column(name = "ad_budget")
    private String adBudget;
    @Column(name = "amount_paid_out")
    private String amountPaidOut;

    private String location;
    private String gender;

    @Column(name = "voucher_validity_end_date")
    private LocalDateTime voucherValidityEndDate;

    @Column(name = "age_group")
    private String ageGroup;
    @Column(name = "target_group")
    private Boolean targetGroup;
    @Column(name = "target_groups")
    private String targetGroups;

    @Column(name = "reward_text")
    private String rewardText;

    public RewardedAudioAds(Long id, String title, String description, String filePath, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.image = image;
    }
}
