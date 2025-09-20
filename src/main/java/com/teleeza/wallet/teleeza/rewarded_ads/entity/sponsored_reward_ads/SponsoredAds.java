package com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
public class SponsoredAds {

    private Long id;

    private String title;
    private String description;
    private String filePath;

    private String image;

    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String answer;
    private String amount;

    private String industry;
    private String company;
    private String duration;
    private String ad_type;

    private String brandLogo;

    private String callToAction;

    private String callToActionTextColor;

    private String callToActionBackgroundColor;

    private String callToActionUrl;

    private String callToActionContact;


    private String opinionQuestion;


    private String whatsAppContact;


    private Boolean isWatched;
    private Boolean isApproved;
    private Boolean isClosed;


    private Integer totalViews;
    private String createdBy;
    private String rewardType;

    private String location;
    private String gender;
    private String age_group;
    private boolean target_group;
    private String target_groups;
    private String reward_text;

    public SponsoredAds(Long id, String title, String description, String filePath, String image, String question, String choiceA, String choiceB, String choiceC, String answer, String amount, String industry,
                        String company, String duration, String ad_type, String brandLogo, String callToAction,  String callToActionUrl,
                        String callToActionContact, String opinionQuestion, String whatsAppContact, Boolean isWatched, Boolean isApproved, Boolean isClosed, Integer totalViews, String createdBy, String rewardType,
                        String location, String gender, String age_group, boolean target_group, String target_groups, String reward_text) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.image = image;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.answer = answer;
        this.amount = amount;
        this.industry = industry;
        this.company = company;
        this.duration = duration;
        this.ad_type = ad_type;
        this.brandLogo = brandLogo;
        this.callToAction = callToAction;
        this.callToActionUrl = callToActionUrl;
        this.callToActionContact = callToActionContact;
        this.opinionQuestion = opinionQuestion;
        this.whatsAppContact = whatsAppContact;
        this.isWatched = isWatched;
        this.isApproved = isApproved;
        this.isClosed = isClosed;
        this.totalViews = totalViews;
        this.createdBy = createdBy;
        this.rewardType = rewardType;
        this.location = location;
        this.gender = gender;
        this.age_group = age_group;
        this.target_group = target_group;
        this.target_groups = target_groups;
        this.reward_text = reward_text;
    }


    public SponsoredAds() {

    }

}
