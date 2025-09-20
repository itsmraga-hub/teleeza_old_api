package com.teleeza.wallet.teleeza.topups.send_airtime.service;

import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.requests.SendAirtimeRequest;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.SendAirtimeResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.models.AirtimeAdRewards;

import java.util.List;

public interface SendAirtimeService{

    SendAirtimeResponse sendAirtime(SendAirtimeRequest sendAirtimeRequest);

    List<AirtimeAdRewards> getAllAirtimeTextRewards(String mobile, String adType);
    List<AirtimeAdRewards> getAllAirtimeVideoRewards(String mobile, String adType);
    List<AirtimeAdRewards> getAllAirtimeAudioRewards(String mobile, String adType);


//    List<AirtimeAdRewards> getAllAirtimeTextRewardsOrderByIdDesc(String phoneNumber, String text);
//    List<AirtimeAdRewards> getAllAirtimeAudioRewardsOrderByIdDesc(String phoneNumber, String audio);
//    List<AirtimeAdRewards> getAllAirtimeVideoRewardsOrderByIdDesc(String phoneNumber, String video);
}
