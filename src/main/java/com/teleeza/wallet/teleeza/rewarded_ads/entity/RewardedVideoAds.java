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

@Entity
@Table(name = "rewarded_video_ads")
@Data
@NoArgsConstructor
@Getter
@Setter

@SqlResultSetMapping(
        name = "filteredRewardedVideoAdsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = VideoAds.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "file_path", type = String.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "question", type = String.class),
                                @ColumnResult(name = "choiceA", type = String.class),
                                @ColumnResult(name = "choiceB", type = String.class),
                                @ColumnResult(name = "choiceC", type = String.class),
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
                        }
                )
        }
)
@NamedNativeQuery(
        name = "filteredRewardedVideos",
        resultClass = VideoAds.class,
//        query = "select u1.first_name as firstName , u1.surname as surName, u1.photo_url as photoUrl, count(u2.referredby_code) as referrals ,  (select count(tc.referredby_code) from users tc where tc.created_on = curdate()) as todaysReferrals from users u1 left join users u2 on u1.referral_code = u2.referredby_code  where  u2.referredby_code is not null and u1.is_staff is false or u1.is_staff is null group by u1.account_number order by referrals desc;\n",
        query = "SELECT\n" +
                "    rewarded_video_ads.id,\n" +
                "    rewarded_video_ads.title,\n" +
                "    rewarded_video_ads.description,\n" +
                "    rewarded_video_ads.file_path,\n" +
                "    rewarded_video_ads.image,\n" +
                "    rewarded_video_ads.question,\n" +
                "    rewarded_video_ads.choicea,\n" +
                "    rewarded_video_ads.choiceb,\n" +
                "    rewarded_video_ads.choicec,\n" +
                "    rewarded_video_ads.answer,\n" +
                "    rewarded_video_ads.amount,\n" +
                "    rewarded_video_ads.target_amount,\n" +
                "    rewarded_video_ads.industry,\n" +
                "    rewarded_video_ads.company,\n" +
                "    rewarded_video_ads.duration,\n" +
                "    rewarded_video_ads.brand_logo,\n" +
                "    rewarded_video_ads.call_to_action,\n" +
                "    rewarded_video_ads.call_to_action_text_color,\n" +
                "    rewarded_video_ads.call_to_action_background_color,\n" +
                "    rewarded_video_ads.call_to_action_url,\n" +
                "    rewarded_video_ads.call_to_action_contact,\n" +
                "    rewarded_video_ads.opinion_question,\n" +
                "    rewarded_video_ads.whatsapp_contact,\n" +
                "    rewarded_video_ads.total_views,\n" +
                "    rewarded_video_ads.is_approved,\n" +
                "    rewarded_video_ads.is_closed,\n" +
                "    rewarded_video_ads.created_by,\n" +
                "    rewarded_video_ads.reward_type,\n" +
                "    rewarded_video_ads.location,\n" +
                "    rewarded_video_ads.gender,\n" +
                "    rewarded_video_ads.age_group,\n" +
                "    rewarded_video_ads.target_group,\n" +
                "    rewarded_video_ads.target_groups,\n" +
                "    (CASE\n" +
                "        WHEN rewarded_videos.count > 0  THEN 'true'\n" +
                "        ELSE 'false'\n" +
                "    END) AS isWatched\n" +
                "FROM rewarded_video_ads\n" +
                "LEFT JOIN (\n" +
                "    SELECT advert_id,COUNT(*) as count\n" +
                "    FROM rewarded_videos\n" +
                "    WHERE phone = :phone\n" +
                "    GROUP BY advert_id\n" +
                ") rewarded_videos\n" +
                "ON rewarded_video_ads.id = rewarded_videos.advert_id\n"+
                "WHERE rewarded_video_ads.end_date >= CURRENT_DATE()\n" +
                "AND rewarded_video_ads.start_date <= CURRENT_DATE()\n" +
                "AND  rewarded_video_ads.industry = :industry\n" +
                "AND rewarded_video_ads.is_approved = true\n" +
//                "AND rewarded_video_ads.is_closed = false\n" +
                "ORDER BY rewarded_video_ads.created_at DESC, isWatched ASC",

        resultSetMapping = "filteredRewardedVideoAdsMapping"
)


@SqlResultSetMapping(
        name = "rewardedVideoAdsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = VideoAds.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "file_path", type = String.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "question", type = String.class),
                                @ColumnResult(name = "choiceA", type = String.class),
                                @ColumnResult(name = "choiceB", type = String.class),
                                @ColumnResult(name = "choiceC", type = String.class),
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
                        }
                )
        }
)
@NamedNativeQuery(
        name = "rewardedVideos",
        resultClass = VideoAds.class,
        query = "SELECT\n" +
                "    rewarded_video_ads.id,\n" +
                "    rewarded_video_ads.title,\n" +
                "    rewarded_video_ads.description,\n" +
                "    rewarded_video_ads.file_path,\n" +
                "    rewarded_video_ads.image,\n" +
                "    rewarded_video_ads.question,\n" +
                "    rewarded_video_ads.choicea,\n" +
                "    rewarded_video_ads.choiceb,\n" +
                "    rewarded_video_ads.choicec,\n" +
                "    rewarded_video_ads.answer,\n" +
                "    rewarded_video_ads.amount,\n" +
                "    rewarded_video_ads.target_amount,\n" +
                "    rewarded_video_ads.industry,\n" +
                "    rewarded_video_ads.company,\n" +
                "    rewarded_video_ads.duration,\n" +
                "    rewarded_video_ads.brand_logo,\n" +
                "    rewarded_video_ads.call_to_action,\n" +
                "    rewarded_video_ads.call_to_action_text_color,\n" +
                "    rewarded_video_ads.call_to_action_background_color,\n" +
                "    rewarded_video_ads.call_to_action_url,\n" +
                "    rewarded_video_ads.call_to_action_contact,\n" +
                "    rewarded_video_ads.opinion_question,\n" +
                "    rewarded_video_ads.whatsapp_contact,\n" +
                "    rewarded_video_ads.total_views,\n" +
                "    rewarded_video_ads.is_approved,\n" +
                "    rewarded_video_ads.is_closed,\n" +
                "    rewarded_video_ads.created_by,\n" +
                "    rewarded_video_ads.reward_type,\n" +
                "    rewarded_video_ads.location,\n" +
                "    rewarded_video_ads.gender,\n" +
                "    rewarded_video_ads.age_group,\n" +
                "    rewarded_video_ads.target_group,\n" +
                "    rewarded_video_ads.target_groups,\n" +
                "    (CASE\n" +
                "        WHEN  rewarded_videos.count > 0 THEN 'true'\n" +
                "        ELSE 'false'\n" +
                "    END) AS isWatched\n" +
                "FROM rewarded_video_ads\n" +
                "LEFT JOIN (\n" +
                "    SELECT advert_id, COUNT(*) as count\n" +
                "    FROM rewarded_videos\n" +
                "    WHERE phone = :phone\n" +
                "    GROUP BY advert_id\n" +
                ") rewarded_videos\n" +
                "ON rewarded_video_ads.id = rewarded_videos.advert_id\n"+
                "WHERE rewarded_video_ads.end_date >= CURRENT_DATE()\n" +
                "AND rewarded_video_ads.start_date <= CURRENT_DATE()\n" +
                "AND rewarded_video_ads.is_approved = true\n" +
//                "AND rewarded_video_ads.is_closed = false\n" +
                "ORDER BY  rewarded_video_ads.created_at DESC, isWatched ASC",

        resultSetMapping = "rewardedVideoAdsMapping"
)
public class RewardedVideoAds {

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

    @Column(name = "question" )
    @Size(max = 200)
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String answer;
    private String location;
    private String amount;
    @Column(name = "target_amount")
    private Integer targetAmount;
    @Column(name = "industry")
    private String industry;

    @Column(name = "company")
    private String company;
    private String duration;
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
    private Integer totalViews;
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

    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "brand")
    private String brand;
    @Column(name = "ad_budget")
    private String adBudget;

    private String gender;

    @Column(name = "voucher_validity_end_date")
    private LocalDateTime voucherValidityEndDate;

    @Column(name = "age_group")
    private String ageGroup;
    @Column(name = "target_group")
    private Boolean targetGroup;
    @Column(name = "target_groups")
    private String targetGroups;

    public RewardedVideoAds(
            Long id,
            String title,
            String description,
            String filePath,
            String image
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.image = image;
    }
}
