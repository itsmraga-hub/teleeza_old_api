package com.teleeza.wallet.teleeza.rewarded_ads.utils;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedVideoAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.VideoAds;

import java.util.Comparator;

public class RewardedVideoAdsComparator implements Comparator<VideoAds> {


    @Override
    public int compare(VideoAds rewardedVideoAds, VideoAds t1) {
        if (rewardedVideoAds.getIndustry().equals(t1.getIndustry())) {
            return rewardedVideoAds.getCompany().compareTo(t1.getCompany());
        } else {
            return rewardedVideoAds.getIndustry().compareTo(t1.getIndustry());
        }
    }
}
