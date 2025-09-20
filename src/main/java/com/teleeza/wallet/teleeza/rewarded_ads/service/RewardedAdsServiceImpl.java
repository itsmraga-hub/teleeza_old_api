package com.teleeza.wallet.teleeza.rewarded_ads.service;

import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.daraja.service.DarajaApi;
import com.teleeza.wallet.teleeza.rewarded_ads.dtos.Request.RewardRequest;
import com.teleeza.wallet.teleeza.rewarded_ads.dtos.response.UserAdRewards;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.*;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredRewardAnswers;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity.Merchant;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity.RedeemedVouchers;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository.MerchantRepository;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository.RedeemedVouchersRepository;
import com.teleeza.wallet.teleeza.rewarded_ads.repository.*;
import com.teleeza.wallet.teleeza.topups.send_airtime.repository.EtopUpAirtimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardedAdsServiceImpl {

    private final RewardedAudioRepository rewardedAudioRepository;

    private final RewardedTextsRepository rewardedTextsRepository;
    private final SponsoredAnswersRepository sponsoredAnswersRepository;
    private final RewardedVideoRepository rewardedVideoRepository;
    private final VouchersRepository vouchersRepository;
    private final DarajaApi darajaApi;
    private final RedeemedVouchersRepository redeemedVouchersRepository;
    private final AdvantaSmsApiImpl advantaSmsApi;
    private final EtopUpAirtimeRepository etopUpAirtimeRepository;
    @Autowired
    private RewardedTextAdRepository rewardedTextAdRepository;
    @Autowired
    private MerchantRepository merchantRepository;

    public UserAdRewards getAlluserAdRewards(String phone) {
        UserAdRewards userAdRewards = new UserAdRewards();
        List<Object> rewardList = new ArrayList<>();
        rewardList.addAll(rewardedAudioRepository.getAllByPhone(phone));
        rewardList.addAll(rewardedVideoRepository.getAllByPhone(phone));
        rewardList.addAll(rewardedTextsRepository.getAllByPhone(phone));
        rewardList.addAll(sponsoredAnswersRepository.getAllByPhone(phone));

        userAdRewards.setRewards(rewardList);

        Collections.shuffle(rewardList);

        return userAdRewards;
    }

    public List<AudioRewards> getUserAudioAdRewards(String phoneNumber) {
        List<AudioRewards> audioRewards;
        audioRewards = rewardedAudioRepository.getAllByPhoneOrderByIdDesc(phoneNumber);

        return audioRewards;
    }

    public List<VideoRewards> getUserVideoAdRewards(String phoneNumber) {
        List<VideoRewards> videoRewards;
        videoRewards = rewardedVideoRepository.getAllByPhoneOrderByIdDesc(phoneNumber);

        return videoRewards;
    }

    public List<TextRewards> getUserTextAdRewards(String phoneNumber) {
        List<TextRewards> textRewards;
        textRewards = rewardedTextsRepository.getAllByPhoneOrderByIdDesc(phoneNumber);

        return textRewards;
    }

    public List<SponsoredRewardAnswers> getUserSponsoredRewards(String phoneNumber) {
        List<SponsoredRewardAnswers> sponsoredRewardAnswers = new ArrayList<SponsoredRewardAnswers>();
        sponsoredRewardAnswers = sponsoredAnswersRepository.getAllByPhoneOrderByIdDesc(phoneNumber);

        return sponsoredRewardAnswers;
    }



    @Transactional
    public Map<String, Object> validateVoucher(String voucherCode, String phone, String amount, String userPhone, String adType, Long adID) {
        // Optional<Voucher> voucher = vouchersRepository.findByCode(voucherCode);
        Optional<Voucher> voucher = vouchersRepository.findVoucherByCodeAndAdvertIdAndPhoneNumberAndAdType(voucherCode, adID, userPhone, adType);
        Map<String, Object> map = new HashMap<>();

        Merchant merchant = merchantRepository.findMerchantByPhone(phone);
        //if (voucher.isPresent() && merchant.getCompany().equals(voucher.get().getCompany()))
        if (voucher.isPresent() && merchant.getMerchantType().equals("Open")) {
            if (voucher.get().isVoucherValid()) {
                map.put("isVoucherValid", true);
                map.put("message", "success");
                map.put("statusCode", "0");
                // send money to merchant
                // Commented out merchant payouts
               /* darajaApi.creditMerchantForRedeemedVouchers(
                        voucherCode, "254" + phone.substring(1),
                        amount
                );*/
                // once merchant has been credited, set voucherValid to false
                voucher.get().setVoucherValid(false);
                voucher.get().setRedeemedAt(LocalDateTime.now());
                vouchersRepository.save(voucher.get());

                // send sms/notification to merchant that they have redeemed a voucher
                /*advantaSmsApi.sendSmsNotification(
                        "Congrats " + merchant.getMerchantName() + ", you have just earned Ksh 100/= via Teleeza Reward Ads for " + voucher.get().getAdTitle() + ". Keep scanning to keep earning!",
                        "254" + phone.substring(1)
                );*/
                // send sms/notification to user that they have redeemed a voucher
                advantaSmsApi.sendSmsNotification(
                        "Congrats, you’ve successfully redeemed eVoucher code " + voucherCode + " for " + voucher.get().getAdTitle() + " Ad. Keep scanning and grow with Teleeza Reward Ads!",
                        "254" + phone.substring(1)
                );
                voucher.get().getPhoneNumber();

                RedeemedVouchers redeemedVouchers = new RedeemedVouchers();
                redeemedVouchers.setCode(voucher.get().getCode());
                redeemedVouchers.setRedeemedAt(LocalDateTime.now());
                redeemedVouchers.setAdTitle(voucher.get().getAdTitle());
                redeemedVouchers.setAdType(voucher.get().getAdType());
                redeemedVouchers.setPhoneNumber(phone);
                redeemedVouchers.setValue(voucher.get().getValue());
                redeemedVouchers.setClientPhoneNumber(userPhone);
                redeemedVouchers.setAdvertId(adID);
                redeemedVouchers.setResultCode(0L);

                redeemedVouchersRepository.save(redeemedVouchers);
                return map;
            } else {
                map.put("isVoucherValid", false);
                map.put("message", "Voucher has expired, redeemed or is invalid");
                map.put("statusCode", "1");
                // send money to merchant
                // once merchant has been credited, set voucherValid to false
                return map;
            }
        } else if (voucher.isPresent() && merchant.getMerchantType().equals("Closed")) {
            if (voucher.get().isVoucherValid() && merchant.getCompany().equals(voucher.get().getCompany())) {
                map.put("isVoucherValid", true);
                map.put("message", "success");
                map.put("statusCode", "0");
                // send money to merchant
                // Commented out merchant payouts
                /* darajaApi.creditMerchantForRedeemedVouchers(
                        voucherCode, "254" + phone.substring(1),
                        amount
                );*/
                // once merchant has been credited, set voucherValid to false
                voucher.get().setVoucherValid(false);
                voucher.get().setRedeemedAt(LocalDateTime.now());
                vouchersRepository.save(voucher.get());

                // send sms/notification to merchant that they have redeemed a voucher
                advantaSmsApi.sendSmsNotification(
                        "Congrats " + merchant.getMerchantName() + ", you have just earned Ksh 100/= via Teleeza Reward Ads for " + voucher.get().getAdTitle() + ". Keep scanning to keep earning!",
                        "254" + phone.substring(1)
                );

                RedeemedVouchers redeemedVouchers = new RedeemedVouchers();
                redeemedVouchers.setCode(voucher.get().getCode());
                redeemedVouchers.setRedeemedAt(LocalDateTime.now());
                redeemedVouchers.setAdTitle(voucher.get().getAdTitle());
                redeemedVouchers.setAdType(voucher.get().getAdType());
                redeemedVouchers.setPhoneNumber(phone);
                redeemedVouchers.setValue(voucher.get().getValue());
                redeemedVouchers.setClientPhoneNumber(userPhone);
                redeemedVouchers.setAdvertId(adID);
                redeemedVouchers.setResultCode(0L);

                redeemedVouchersRepository.save(redeemedVouchers);
                return map;
            } else {
                map.put("isVoucherValid", false);
                map.put("message", "Voucher has expired, redeemed , is invalid or you are not authorized to redeem this voucher");
                map.put("statusCode", "1");
                // send money to merchant
                // once merchant has been credited, set voucherValid to false
                return map;
            }

        } else {
            map.put("isVoucherValid", false);
            map.put("message", "Voucher does not exist or you are not an authorized merchant to redeem this eVoucher");
            map.put("statusCode", "1");
            return map;
        }
    }

    public Map<String, Object> getUserRewardedAirtime(String mobile) {
        Map<String, Object> map = new HashMap<>();
        map.put("airtime", etopUpAirtimeRepository.getAirtimeAdRewardsByMobile(mobile));
        return map;
    }

    public Map<String, Object> getUserVouchers(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("vouchers", vouchersRepository.findAllByPhoneNumber(phone));
        return map;
    }

    public Map<String, Object> getMerchantRedeemedVouchers(String phone, long dayRange) {
        Map<String, Object> map = new HashMap<>();
        map.put("vouchers", redeemedVouchersRepository.getRecentTenVouchers(phone));
        map.put("totalEarned", redeemedVouchersRepository.totalRedeemedByMerchant(phone, dayRange));
        return map;
    }

    public Map<String, Object> getAllMerchantRedeemedVouchers(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("vouchers", redeemedVouchersRepository.findAllByPhoneNumber(phone));
        return map;
    }

    public Map<String, Object> getAllTextAdsMerchantRedeemedVouchers(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("vouchers", redeemedVouchersRepository.findAllByPhoneNumberAndAdTypeOrderByIdDesc(phone, "text"));
        return map;
    }

    public Map<String, Object> getAllAudioAdsMerchantRedeemedVouchers(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("vouchers", redeemedVouchersRepository.findAllByPhoneNumberAndAdTypeOrderByIdDesc(phone, "audio"));
        return map;
    }

    public Map<String, Object> getAllVideoAdsMerchantRedeemedVouchers(String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("vouchers", redeemedVouchersRepository.findAllByPhoneNumberAndAdTypeOrderByIdDesc(phone, "video"));
        return map;
    }



    public Map<String, String> sendTextAdReward(RewardRequest rewardRequest) {
        Map<String, String> response = new HashMap<String, String>();

        Optional<RewardedTextAd> rewardedTextAd = rewardedTextAdRepository.findById(
                Long.parseLong(rewardRequest.getAdvertId())
        );

        if(rewardedTextAd.isPresent()) {
            rewardedTextAd.get().setTotalViews(+1);
            rewardedTextAdRepository.save(rewardedTextAd.get());
            rewardedTextAdRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
            if (rewardedTextAd.get().getIsClosed()) {
                response.put("message", "The ad has been closed");
                response.put("statusCode", "1");
                return response;
            }
        }

        return response;

    }
}
