package com.teleeza.wallet.teleeza.rewarded_ads.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TextAds {
    private Long id;
    private String title;
    private String description;

    private String image;

    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String answer;
    private String amount;
    private Integer target_amount;
    private String industry;
    private String company;
    private String backgroundColor;
    private String textColor;
    private String adType;
    private String duration;
//    private String location;
    private String brandLogo;
    private String callToAction;

    private String callToActionTextColor;

    private String callToActionBackgroundColor;


    private String callToActionUrl;

    private String callToActionContact;

    private String questionBtnBackgroundColor;

    private String questionButtonTextColor;

    private String whatsAppContact;

    private String opinionQuestion;

    private Boolean isWatched;
    private Boolean isApproved;
    private Boolean isClosed;


    private Integer totalViews ;
    private String createdBy;
    private String rewardType;

    private String thumbnail;

    private String location;
    private String gender;
    private String age_group;
    private boolean target_group;
    private String target_groups;
    private String reward_text;
    public TextAds(Long id, String title,
                   String description,
                   String image, String question,
                   String choiceA, String choiceB,
                   String choiceC, String answer,
                   String amount, Integer target_amount, String industry,String company,
                   String backgroundColor, String textColor,
                   String adType, String duration,
                   String brandLogo, String callToAction,

                   String callToActionTextColor, String callToActionBackgroundColor,
                   String callToActionUrl, String callToActionContact,
                   String questionBtnBackgroundColor,
                   String questionButtonTextColor,
                   String whatsAppContact, String opinionQuestion,
                   Boolean isWatched,
                   Boolean isApproved,Boolean isClosed,
                   Integer totalViews,
                   String createdBy,
                   String rewardType,
                   String thumbnail,
                   String location,
                   String gender,
                   String age_group,
                   boolean target_group,
                   String target_groups,
                   String reward_text
                   ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.answer = answer;
        this.amount = amount;
        this.target_amount = target_amount;
        this.industry = industry;
        this.company = company;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.adType = adType;
        this.duration = duration;
        this.brandLogo = brandLogo;
        this.callToAction = callToAction;
        this.callToActionTextColor = callToActionTextColor;
        this.callToActionBackgroundColor = callToActionBackgroundColor;
        this.callToActionUrl = callToActionUrl;
        this.callToActionContact = callToActionContact;
        this.questionBtnBackgroundColor = questionBtnBackgroundColor;
        this.questionButtonTextColor = questionButtonTextColor;
        this.whatsAppContact = whatsAppContact;
        this.opinionQuestion = opinionQuestion;
        this.isWatched = isWatched;
        this.isApproved = isApproved;
        this.isClosed = isClosed;
        this.totalViews = totalViews;
        this.createdBy = createdBy;
        this.rewardType = rewardType;
        this.thumbnail = thumbnail;
        this.location = location;
        this.gender = gender;
        this.age_group = age_group;
        this.target_group = target_group;
        this.target_groups = target_groups;
        this.reward_text = reward_text;
    }

    public TextAds() {
    }
}
