package com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sponsored_reward_answers")
@Data
public class SponsoredRewardAnswers{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int amount;
	private String gender;
	@Column(name = "advert_title")
	private String advertTitle;
	@Column(name = "advert_type")
	private String advertType;
	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();
	private String phone;
	@Column(name = "reward_type")
	private String rewardType;
	@Column(name = "result_code")
	private int resultCode;
	private String location;
	@Column(name = "opinion_answer")
	private String opinionAnswer;
	private int views;
	private int age;
	@Column(name = "advert_id")
	private int advertId;
}