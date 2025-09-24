package com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.TextAds;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sponsored_reward_ads")
@Data
@NoArgsConstructor
@SqlResultSetMapping(
		name = "filteredSponsoredRewardAdsMapping",
		classes = {
				@ConstructorResult(
						targetClass = SponsoredAds.class,
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
								@ColumnResult(name = "industry", type = String.class),
								@ColumnResult(name = "company", type = String.class),
								@ColumnResult(name = "duration", type = String.class),
								@ColumnResult(name = "text_color", type = String.class),
								@ColumnResult(name = "ad_type", type = String.class),
								@ColumnResult(name = "brand_logo", type = String.class),
								@ColumnResult(name = "call_to_action", type = String.class),
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
                                @ColumnResult(name = "reward_text", type = String.class),
                        }
				)
		}
)

@SqlResultSetMapping(
		name = "sponsoredRewardAdsMapping",
		classes = {
				@ConstructorResult(
						targetClass = SponsoredAds.class,
						columns = {
								@ColumnResult(name = "id", type = Long.class),
								@ColumnResult(name = "title", type = String.class),
								@ColumnResult(name = "description", type = String.class),
								@ColumnResult(name = "file_path", type = String.class),
								@ColumnResult(name = "image", type = String.class),
								@ColumnResult(name = "question", type = String.class),
								@ColumnResult(name = "choicea", type = String.class),
								@ColumnResult(name = "choiceb", type = String.class),
								@ColumnResult(name = "choicec", type = String.class),
								@ColumnResult(name = "answer", type = String.class),
								@ColumnResult(name = "amount", type = String.class),
								@ColumnResult(name = "industry", type = String.class),
								@ColumnResult(name = "company", type = String.class),
								@ColumnResult(name = "duration", type = String.class),
								@ColumnResult(name = "ad_type", type = String.class),
								@ColumnResult(name = "brand_logo", type = String.class),
								@ColumnResult(name = "call_to_action", type = String.class),
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
		name = "sponsoredRewardAds",
		resultClass = SponsoredAds.class,
		query = "SELECT\n" +
				"    sponsored_reward_ads.id,\n" +
				"    sponsored_reward_ads.title,\n" +
				"    sponsored_reward_ads.description,\n" +
				"    sponsored_reward_ads.file_path,\n" +
				"    sponsored_reward_ads.image,\n" +
				"    sponsored_reward_ads.question,\n" +
				"    sponsored_reward_ads.choicea,\n" +
				"    sponsored_reward_ads.choiceb,\n" +
				"    sponsored_reward_ads.choicec,\n" +
				"    sponsored_reward_ads.answer,\n" +
				"    sponsored_reward_ads.amount,\n" +
				"    sponsored_reward_ads.industry,\n" +
				"    sponsored_reward_ads.company,\n" +
				"    sponsored_reward_ads.duration,\n" +
				"    sponsored_reward_ads.ad_type,\n" +
				"    sponsored_reward_ads.brand_logo,\n" +
				"    sponsored_reward_ads.call_to_action,\n" +
				"    sponsored_reward_ads.call_to_action_url,\n" +
				"    sponsored_reward_ads.call_to_action_contact,\n" +
				"    sponsored_reward_ads.opinion_question,\n" +
				"    sponsored_reward_ads.whatsapp_contact,\n" +
				"    sponsored_reward_ads.total_views,\n" +
				"    sponsored_reward_ads.is_approved,\n" +
				"    sponsored_reward_ads.is_closed,\n" +
				"    sponsored_reward_ads.created_by,\n" +
				"    sponsored_reward_ads.reward_type,\n" +
				"    sponsored_reward_ads.location,\n" +
				"    sponsored_reward_ads.gender,\n" +
				"    sponsored_reward_ads.age_group,\n" +
				"    sponsored_reward_ads.target_group,\n" +
				"    sponsored_reward_ads.target_groups,\n" +
				"    sponsored_reward_ads.reward_text,\n" +
				"    (CASE\n" +
				"        WHEN sponsored_reward_answers.count > 0 THEN 'true'\n" +
				"        ELSE 'false'\n" +
				"    END) AS isWatched\n" +
				"FROM sponsored_reward_ads\n" +
				"LEFT JOIN (\n" +
				"    SELECT advert_id, COUNT(*) as count\n" +
				"    FROM sponsored_reward_answers\n" +
				"    WHERE phone = ?1\n" +
				"    GROUP BY advert_id\n" +
				") sponsored_reward_answers\n" +
				"ON sponsored_reward_ads.id = sponsored_reward_answers.advert_id\n" +
				"WHERE sponsored_reward_ads.end_date >= CURRENT_DATE() \n" +
				"AND sponsored_reward_ads.start_date <= CURRENT_DATE()\n" +
				"AND sponsored_reward_ads.is_approved = true\n" +
				"ORDER BY sponsored_reward_ads.created_at DESC, isWatched ASC",

		resultSetMapping = "sponsoredRewardAdsMapping"
)
public class SponsoredRewardAds{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "file_path")
	private String filePath;
	private String reason;
	@Column(name = "target_amount")
	private int targetAmount;
	private String adType;
	@Column(name = "call_to_action")
	private String callToAction;
	@Column(name = "total_views")
	private int totalViews;
	@Column(name = "brand_color")
	private String brandColor;
	@Column(name = "brand_logo")
	private String brandLogo;
	@Column(name = "mpesa_ad_amount")
	private int mpesaAdAmount;
	private String objective;
	@Column(name = "call_to_action_contact")
	private String callToActionContact;

	private String brand;
	private boolean isWatched;
	@Column(name = "is_closed")
	private boolean isClosed;
	private String image;
	private String thumbnail;
	@Column(name = "voucher_validity_end_date")
	private String voucherValidityEndDate;
	@Column(name = "choicec")
	private String choicec;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "choicea")
	private String choicea;
	@Column(name = "choiceb")
	private String choiceb;
	@Column(name = "background_image")
	private String backgroundImage;
	@Column(name = "redeem_method")
	private String redeemMethod;
	@Column(name = "approved_by")
	private int approvedBy;
	@Column(name = "mpesa_code")
	private String mpesaCode;
	@Column(name = "discount_off")
	private String discountOff;
	@Column(name = "text_color")
	private String textColor;
	private String slogan;
	@Column(name = "end_date")
	private String endDate;
	@Column(name = "redeemed_at")
	private String redeemedAt;
	private String gender;
	@Column(name = "reward_type")
	private String rewardType;
	private String description;
	@Column(name = "created_at")
	private String createdAt;
	private String industry;
	private String title;
	@Column(name = "ad_budget")
	private int adBudget;
	private String duration;
	@Column(name = "call_to_action_url")
	private String callToActionUrl;
	@Column(name = "whatsapp_contact")
	private String whatsappContact;
	@Column(name = "updated_at")
	private String updatedAt;
	private String company;
	@Column(name = "start_date")
	private String startDate;
	private String amount;
	@Column(name = "teleeza_commission")
	private int teleezaCommission;
	private String question;
	private String agency;
	@Column(name = "age_group")
	private String ageGroup;
	private String lpo;
	@Column(name = "ad_pay_number")
	private String adPayNumber;
	private String answer;
	@Column(name = "background_color")
	private String backgroundColor;
	@Column(name = "agency_commission")
	private int agencyCommission;
	@Column(name = "is_approved")
	private boolean isApproved;
	private String location;
	@Column(name = "amount_paid_out")
	private int amountPaidOut;
	@Column(name = "opinion_question")
	private String opinionQuestion;

    @Column(name = "reward_text")
    private String rewardText;
}