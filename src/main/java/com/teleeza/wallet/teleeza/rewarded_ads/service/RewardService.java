package com.teleeza.wallet.teleeza.rewarded_ads.service;

import com.teleeza.wallet.teleeza.MQTT.PublishUser;
import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.daraja.service.DarajaApi;
import com.teleeza.wallet.teleeza.rewarded_ads.dtos.Request.RewardRequest;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.*;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredRewardAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredRewardAnswers;
import com.teleeza.wallet.teleeza.rewarded_ads.repository.*;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.requests.SendAirtimeRequest;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.TupayAirtimeBalanceResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.service.IAirtimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class RewardService {
    private final VouchersRepository vouchersRepository;
    private final RewardedVideoAdRepository rewardedVideoAdRepository;
    private final IAirtimeService tupayAirtimeService;
    private final RewardedAudioAdsRepository rewardedAudioAdsRepository;
    private final RewardedTextAdRepository rewardedTextAdRepository;
    private final RewardedVideoRepository rewardedVideoRepository;
    private final RewardedAudioRepository rewardedAudioRepository;
    private final DarajaApi darajaApi;
    private final RewardedTextsRepository rewardedTextsRepository;
    private final VoucherService voucherService;
    private final AdvantaSmsApiImpl advantaSmsApi;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final PublishUser publishUser;
    private final CustomVoucherRepository customVoucherRepository;
    private final SponsoredRewardAdRepository sponsoredRewardAdRepository;
    private final SponsoredAnswersRepository sponsoredAnswersRepository;

    public RewardService(VouchersRepository vouchersRepository, RewardedVideoAdRepository rewardedVideoAdRepository,
                         IAirtimeService tupayAirtimeService, RewardedAudioAdsRepository rewardedAudioAdsRepository,
                         RewardedTextAdRepository rewardedTextAdRepository, RewardedVideoRepository rewardedVideoRepository,
                         RewardedAudioRepository rewardedAudioRepository, DarajaApi darajaApi, RewardedTextsRepository rewardedTextsRepository,
                         VoucherService voucherService, AdvantaSmsApiImpl advantaSmsApi, CustomerRegistrationRepository customerRegistrationRepository,
                         PublishUser publishUser, CustomVoucherRepository customVoucherRepository, SponsoredRewardAdRepository sponsoredRewardAdRepository,
                         SponsoredAnswersRepository sponsoredAnswersRepository) {
        this.vouchersRepository = vouchersRepository;
        this.rewardedVideoAdRepository = rewardedVideoAdRepository;
        this.tupayAirtimeService = tupayAirtimeService;
        this.rewardedAudioAdsRepository = rewardedAudioAdsRepository;
        this.rewardedTextAdRepository = rewardedTextAdRepository;
        this.rewardedVideoRepository = rewardedVideoRepository;
        this.rewardedAudioRepository = rewardedAudioRepository;
        this.darajaApi = darajaApi;
        this.rewardedTextsRepository = rewardedTextsRepository;
        this.voucherService = voucherService;
        this.advantaSmsApi = advantaSmsApi;
        this.customerRegistrationRepository = customerRegistrationRepository;
        this.publishUser = publishUser;
        this.customVoucherRepository = customVoucherRepository;
        this.sponsoredRewardAdRepository = sponsoredRewardAdRepository;
        this.sponsoredAnswersRepository = sponsoredAnswersRepository;
    }

    public ResponseEntity<?> rewardUserEndpoint(RewardRequest rewardRequest) {
        if (rewardRequest.getIsSponsored() == 1) {
            log.info("Rewarding sponsored ad");
            return rewardSponsoredAds(rewardRequest);
        }
        switch (rewardRequest.getRewardType()) {
            case "airtime":
                return rewardAirtime(rewardRequest);
            case "coupon":
                return rewardVoucher(rewardRequest);
            case "cash":
                return  rewardCash(rewardRequest);
            default:
                return ResponseEntity.badRequest().body("Invalid type");
        }
    }

    public ResponseEntity<?> rewardSponsoredAds(RewardRequest rewardRequest) {
        Map<String, String> response = new HashMap<String, String>();

        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + rewardRequest.getPhoneNumber());

        Optional<SponsoredRewardAds> sponsoredRewardAds = sponsoredRewardAdRepository.findById(
                Long.parseLong(rewardRequest.getAdvertId())
        );


        if (sponsoredRewardAds.isPresent()) {
            int targetAudienceNumber = sponsoredRewardAds.get().getTargetAmount() / Integer.parseInt(sponsoredRewardAds.get().getAmount());
            if (targetAudienceNumber == sponsoredRewardAds.get().getTotalViews() || sponsoredRewardAds.get().isClosed()) {
                sponsoredRewardAdRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
                sponsoredRewardAdRepository.save(sponsoredRewardAds.get());
                response.put("statusCode", "1");
                response.put("message", "Reward closed");
                return ResponseEntity.accepted().body(response);
            }

            // check if user has been rewarded already or not/
            SponsoredRewardAnswers rewards = sponsoredAnswersRepository.findByPhoneAndAdvertId(
                    rewardRequest.getPhoneNumber(),
                    Integer.parseInt(rewardRequest.getAdvertId())
            );

            if (rewards != null) {
                response.put("message", "User has already been rewarded");
                response.put("statusCode", "2");
                rewards.setViews(rewards.getViews() + 1);
                sponsoredAnswersRepository.save(rewards);
                return ResponseEntity.accepted().body(response);
            }

            SponsoredRewardAnswers sponsoredRewardAnswers = new SponsoredRewardAnswers();
            int totalViews = sponsoredRewardAds.get().getTotalViews() + 1;
            int amountPaidOut = totalViews * Integer.parseInt(sponsoredRewardAds.get().getAmount());
            sponsoredRewardAds.get().setTotalViews(totalViews);
            sponsoredRewardAds.get().setAmountPaidOut(amountPaidOut);
            sponsoredRewardAdRepository.save(sponsoredRewardAds.get());
            // rewardedTextAdRepository.updateAmountPaidOut(Long.parseLong(rewardRequest.getAdvertId()), rewardRequest.getAmount());
            log.info("User has not been rewarded. send ad reward");
            sponsoredRewardAnswers.setPhone(rewardRequest.getPhoneNumber());
            sponsoredRewardAnswers.setAmount(Math.toIntExact(Long.parseLong(String.valueOf(rewardRequest.getAmount()))));
            sponsoredRewardAnswers.setAdvertType(rewardRequest.getAdType());
            sponsoredRewardAnswers.setAdvertId(Math.toIntExact(Long.parseLong(rewardRequest.getAdvertId())));
            sponsoredRewardAnswers.setOpinionAnswer(rewardRequest.getOpinionAnswer());
            sponsoredRewardAnswers.setViews(1);

            sponsoredRewardAnswers.setRewardType(rewardRequest.getRewardType());
            sponsoredRewardAnswers.setLocation(rewardRequest.getLocation());
            sponsoredRewardAnswers.setAge(Integer.parseInt(rewardRequest.getAge()));
            sponsoredRewardAnswers.setAdvertTitle(sponsoredRewardAds.get().getTitle());
            sponsoredRewardAnswers.setGender(rewardRequest.getGender());
            sponsoredRewardAnswers.setResultCode(0);
            sponsoredAnswersRepository.save(sponsoredRewardAnswers);

            if (Objects.equals(rewardRequest.getRewardType(), "airtime")) {
                SendAirtimeRequest sendAirtimeRequest = new SendAirtimeRequest();
                sendAirtimeRequest.setAmount(String.valueOf(rewardRequest.getAmount()));
                sendAirtimeRequest.setEmail("");
                sendAirtimeRequest.setAdTitle(sponsoredRewardAds.get().getTitle());
                sendAirtimeRequest.setCountry("KE");
                sendAirtimeRequest.setCountry("KES");
                sendAirtimeRequest.setAdType(rewardRequest.getAdType());
                sendAirtimeRequest.setMobile(rewardRequest.getPhoneNumber());
                // sendAirtimeService.sendAirtime(sendAirtimeRequest);
                tupayAirtimeService.sendAirtimeToPerson(rewardRequest.getPhoneNumber(), rewardRequest.getAmount().toString());
                sendAirtimeMessage(customer.getFirstName(), rewardRequest.getAmount(),
                        sponsoredRewardAds.get().getCompany(), sponsoredRewardAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber());
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Text", sponsoredRewardAds.get().getId());
                return ResponseEntity.accepted().body(response);
            } else if (Objects.equals(rewardRequest.getRewardType(), "cash")) {
                darajaApi.creditUserForRewardedAds(rewardRequest);
                sendCashMessage(customer.getFirstName(), rewardRequest.getAmount(),
                        sponsoredRewardAds.get().getCompany(), sponsoredRewardAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber());
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Video", sponsoredRewardAds.get().getId());
            } else if (Objects.equals(rewardRequest.getRewardType(), "coupon")) {
                // CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + rewardRequest.getPhoneNumber());

                List<CustomVoucher> customVouchers = customVoucherRepository.findCustomVouchersByAdvertIdAndAdvertType(Math.toIntExact(sponsoredRewardAds.get().getId()), "sponsored");
                Voucher voucher = new Voucher();
                if (!customVouchers.isEmpty())
                {
                    CustomVoucher customVoucher = new CustomVoucher();
                    if (customVouchers.size() == 1) {
                        customVoucher = customVouchers.get(0);
                        if (customVoucher.getClientPhoneNumber().contains(rewardRequest.getPhoneNumber())) {
                            response.put("message", "User has already been rewarded");
                            response.put("statusCode", "2");
                            return ResponseEntity.accepted().body(response);
                        }
                    /* advantaSmsApi.sendSmsNotification(
                            "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                    rewardedTextAd.get().getCompany() + " valid till " + rewardedTextAd.get().getVoucherValidityEndDate().toString().split("T")[0]
                                    +". Click pilot.tam.co.ke?v=" + customVouchers.get(0).getVoucherCode() + "&c=t&a=" + rewardedTextAd.get().getId() + " to redeem.",
                            rewardRequest.getPhoneNumber()
                    );*/
                    }
                    else {
                        List<CustomVoucher> cVs = customVoucherRepository
                                .findCustomVouchersByAdvertIdAndAdvertTypeAndRedeemed(Math.toIntExact(sponsoredRewardAds.get().getId()), "sponsored", false);
                        customVoucher = cVs.get(0);

                   /* advantaSmsApi.sendSmsNotification(
                            "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                    rewardedTextAd.get().getCompany() + " valid till " + rewardedTextAd.get().getVoucherValidityEndDate().toString().split("T")[0]
                                    + ". Click pilot.tam.co.ke?v=" + customVoucher.getVoucherCode() + "&c=t&a=" + rewardedTextAd.get().getId() + " to redeem.",
                            rewardRequest.getPhoneNumber()
                    );*/
                    }
                    customVoucher.setClientGender(rewardRequest.getGender());
                    customVoucher.setClientLocation(rewardRequest.getLocation());
                    customVoucher.setRedeemed(true);
                    customVoucher.setClientPhoneNumber(rewardRequest.getPhoneNumber());
                    customVoucher.setDateSentOut(LocalDateTime.now());
                    customVoucher.setClientName(customer.getFirstName() + " " + customer.getLastName());
                    customVoucherRepository.save(customVoucher);

                    // Set Voucher to vouchers table
                    voucher.setCode(customVoucher.getVoucherCode());
                    voucher.setValue(rewardRequest.getAmount());
                    voucher.setVoucherValid(true);
                    voucher.setCreatedAt(LocalDateTime.now());
                    voucher.setCompany(sponsoredRewardAds.get().getCompany());
                    voucher.setExpirationDate(LocalDateTime.parse(sponsoredRewardAds.get().getVoucherValidityEndDate().replace(" ", "T")));
                    voucher.setAdType("sponsored");
                    voucher.setSystemGenerated(false);

                    voucher.setAdvertId(Long.parseLong(rewardRequest.getAdvertId()));
                    voucher.setAdTitle(sponsoredRewardAds.get().getTitle());
                    voucher.setPhoneNumber(rewardRequest.getPhoneNumber());
                    voucher = vouchersRepository.save(voucher);
                }
                else {

                    // Generate Coupon and send sms notification to user. Update the amount_paid_out column
                    voucher = voucherService.generateVoucher(
                            rewardRequest.getAmount(),
                            sponsoredRewardAds.get().getCompany(),
                            rewardRequest.getPhoneNumber(),
                            sponsoredRewardAds.get().getBrand(),
                            sponsoredRewardAds.get().getTitle(),
                            rewardRequest.getAdType(),
                            Long.valueOf(rewardRequest.getAdvertId()),
                            sponsoredRewardAds.get().getBrandLogo()
                    );
                }
                /*advantaSmsApi.sendSmsNotification(
                        "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                sponsoredRewardAds.get().getCompany() + " valid till " + sponsoredRewardAds.get().getVoucherValidityEndDate().toString().split("T")[0]
                                +". Click pilot.tam.co.ke?v=" + voucher.getCode() + "&c=t&a=" + sponsoredRewardAds.get().getId()
                                + "&d=" + voucher.getId() + " to redeem.",
                        rewardRequest.getPhoneNumber()
                );*/
                if (sponsoredRewardAds.get().getIndustry().equals("12")) {
                    sendGamingVoucherMessage(customer.getFirstName(), sponsoredRewardAds.get().getCompany(), sponsoredRewardAds.get().getVoucherValidityEndDate().substring(0, 11),
                            sponsoredRewardAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber(), sponsoredRewardAds.get().getRewardText());
                } else {
//                    sendVoucherMessage(customer.getFirstName(), rewardRequest.getAmount(), sponsoredRewardAds.get().getCompany(),
//                            sponsoredRewardAds.get().getVoucherValidityEndDate().split("T")[0],
//                            voucher.getCode(), sponsoredRewardAds.get().getId(), voucher.getId(), sponsoredRewardAds.get().getDiscountOff(),
//                            rewardRequest.getPhoneNumber(), "s", sponsoredRewardAds.get().getRewardText());

                    sendVoucherMessage(customer.getFirstName(), rewardRequest.getAmount(), sponsoredRewardAds.get().getCompany(),
                            sponsoredRewardAds.get().getVoucherValidityEndDate().substring(0, 11),
                            voucher.getCode(), sponsoredRewardAds.get().getId(), voucher.getId(),
                            rewardRequest.getPhoneNumber(), "s", sponsoredRewardAds.get().getRewardText());
                }
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Text", sponsoredRewardAds.get().getId());
                return ResponseEntity.accepted().body(response);
            }
            response.put("message", "Reward has been sent");
            response.put("statusCode", "0");
            return ResponseEntity.accepted().body(response);
        }
        response.put("message", "Sponsored Ad Not Found");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> rewardAirtime(RewardRequest rewardRequest) {
        Map<String, String> response = new HashMap<String, String>();
        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + rewardRequest.getPhoneNumber());

        TupayAirtimeBalanceResponse tupayAirtimeBalanceResponse = tupayAirtimeService.getBalance();
        log.info("Tupay Airtime Balance: " + tupayAirtimeBalanceResponse);
        if (tupayAirtimeBalanceResponse.getAmount() <= rewardRequest.getAmount().doubleValue()) {
            response.put("message", "Sorry! Try Again Later or Contact +254706122122/+254714627627");
            response.put("statusCode", "1");
            return ResponseEntity.accepted().body(response);
        }

        if (Objects.equals(rewardRequest.getAdType(), "text")) {
            Optional<RewardedTextAd> rewardedTextAd = rewardedTextAdRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );

            Map<String, String> responseEntity = checkAndSaveImageRewards(rewardedTextAd, rewardRequest, response);
            // rewardedTextsRepository.save(textRewards);
            if (responseEntity.get("statusCode").equals("0")) {
                SendAirtimeRequest sendAirtimeRequest = new SendAirtimeRequest();
                sendAirtimeRequest.setAmount(String.valueOf(rewardRequest.getAmount()));
                sendAirtimeRequest.setEmail("");
                sendAirtimeRequest.setAdTitle(rewardedTextAd.get().getTitle());
                sendAirtimeRequest.setCountry("KE");
                sendAirtimeRequest.setCountry("KES");
                sendAirtimeRequest.setAdType(rewardRequest.getAdType());
                sendAirtimeRequest.setMobile(rewardRequest.getPhoneNumber());
                // sendAirtimeService.sendAirtime(sendAirtimeRequest);
                tupayAirtimeService.sendAirtimeToPerson(rewardRequest.getPhoneNumber(), rewardRequest.getAmount().toString());
                sendAirtimeMessage(customer.getFirstName(), rewardRequest.getAmount(),
                        rewardedTextAd.get().getCompany(), rewardedTextAd.get().getCallToActionUrl(), rewardRequest.getPhoneNumber());
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Text", rewardedTextAd.get().getId());
            }

            return ResponseEntity.accepted().body(responseEntity);
        }
        else if (Objects.equals(rewardRequest.getAdType(), "video")) {
            Optional<RewardedVideoAds> rewardedVideoAds = rewardedVideoAdRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );

            Map<String, String> responseEntity = checkAndSaveVideoRewards(rewardedVideoAds, rewardRequest, response);

            if (responseEntity.get("statusCode").equals("0")) {
                SendAirtimeRequest sendAirtimeRequest = new SendAirtimeRequest();
                sendAirtimeRequest.setAmount(String.valueOf(rewardRequest.getAmount()));
                sendAirtimeRequest.setEmail("");
                sendAirtimeRequest.setAdTitle(rewardedVideoAds.get().getTitle());
                sendAirtimeRequest.setCountry("KE");
                sendAirtimeRequest.setCountry("KES");
                sendAirtimeRequest.setAdType(rewardRequest.getAdType());
                sendAirtimeRequest.setMobile(rewardRequest.getPhoneNumber());
                // sendAirtimeService.sendAirtime(sendAirtimeRequest);
                tupayAirtimeService.sendAirtimeToPerson(rewardRequest.getPhoneNumber(), rewardRequest.getAmount().toString());
                sendAirtimeMessage(customer.getFirstName(), rewardRequest.getAmount(),
                        rewardedVideoAds.get().getCompany(), rewardedVideoAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber());
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Text", rewardedVideoAds.get().getId());
            }
            return ResponseEntity.accepted().body(response);
        }
        else if (Objects.equals(rewardRequest.getAdType(), "audio")) {
            Optional<RewardedAudioAds> rewardedAudioAds = rewardedAudioAdsRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );

            Map<String, String> responseEntity = checkAndSaveAudioRewards(rewardedAudioAds, rewardRequest, response);

            if (responseEntity.get("statusCode").equals("0")) {
                SendAirtimeRequest sendAirtimeRequest = new SendAirtimeRequest();
                sendAirtimeRequest.setAmount(String.valueOf(rewardRequest.getAmount()));
                sendAirtimeRequest.setEmail("");
                sendAirtimeRequest.setAdTitle(rewardedAudioAds.get().getTitle());
                sendAirtimeRequest.setCountry("KE");
                sendAirtimeRequest.setCountry("KES");
                sendAirtimeRequest.setAdType(rewardRequest.getAdType());
                sendAirtimeRequest.setMobile(rewardRequest.getPhoneNumber());
                tupayAirtimeService.sendAirtimeToPerson(rewardRequest.getPhoneNumber(), rewardRequest.getAmount().toString());
                sendAirtimeMessage(customer.getFirstName(), rewardRequest.getAmount(),
                        rewardedAudioAds.get().getCompany(), rewardedAudioAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber());
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Text", rewardedAudioAds.get().getId());
            }
            return ResponseEntity.accepted().body(response);
        }
        return ResponseEntity.accepted().body(response);
    }

    public ResponseEntity<?> rewardCash(RewardRequest rewardRequest) {
        Map<String, String> response = new HashMap<String, String>();
        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + rewardRequest.getPhoneNumber());

        if (Objects.equals(rewardRequest.getAdType(), "video")) {
            Optional<RewardedVideoAds> rewardedVideoAds = rewardedVideoAdRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );


            Map<String, String> responseEntity = checkAndSaveVideoRewards(rewardedVideoAds, rewardRequest, response);

            if (responseEntity.get("statusCode").equals("0")) {
                darajaApi.creditUserForRewardedAds(rewardRequest);
                sendCashMessage(customer.getFirstName(), rewardRequest.getAmount(),
                        rewardedVideoAds.get().getCompany(), rewardedVideoAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber());
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Video", rewardedVideoAds.get().getId());
            }
            return ResponseEntity.accepted().body(response);
        }
        else if (Objects.equals(rewardRequest.getAdType(), "audio")) {
            Optional<RewardedAudioAds> rewardedAudioAds = rewardedAudioAdsRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );


            Map<String, String> responseEntity = checkAndSaveAudioRewards(rewardedAudioAds, rewardRequest, response);

            if (responseEntity.get("statusCode").equals("0")) {
                darajaApi.creditUserForRewardedAds(rewardRequest);
                // log.info("URL {}", rewardedAudioAds.get().getCallToActionUrl());
                // log.info("URL {}", rewardedAudioAds.get());
                sendCashMessage(customer.getFirstName(), rewardRequest.getAmount(),
                        rewardedAudioAds.get().getCompany(), rewardedAudioAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber());
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Audio", rewardedAudioAds.get().getId());
            }
            return ResponseEntity.accepted().body(response);
        }
        else if (Objects.equals(rewardRequest.getAdType(), "text")) {
            Optional<RewardedTextAd> rewardedTextAd = rewardedTextAdRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );


            Map<String, String> responseEntity = checkAndSaveImageRewards(rewardedTextAd, rewardRequest, response);

            if (responseEntity.get("statusCode").equals("0")) {
                darajaApi.creditUserForRewardedAds(rewardRequest);
                sendCashMessage(customer.getFirstName(), rewardRequest.getAmount(),
                        rewardedTextAd.get().getCompany(), rewardedTextAd.get().getCallToActionUrl(), rewardRequest.getPhoneNumber());
                response.put("message", "Reward has been sent");
                response.put("statusCode", "0");
                rewardUser("Video", rewardedTextAd.get().getId());
            }
            return ResponseEntity.accepted().body(response);
        }
            return ResponseEntity.accepted().body(response);
    }

    public ResponseEntity<?> rewardVoucher(RewardRequest rewardRequest) {
        Map<String, String> response = new HashMap<String, String>();

        log.info("William 3::: Start");
        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + rewardRequest.getPhoneNumber());
        String location = customer.getLocation().toLowerCase().trim();
        String gender = customer.getGender().toLowerCase().trim();
        Voucher voucher = new Voucher();
        if (Objects.equals(rewardRequest.getAdType(), "text")) {
            Optional<RewardedTextAd> rewardedTextAd = rewardedTextAdRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );
            List<CustomVoucher> customVouchers = customVoucherRepository.findCustomVouchersByAdvertIdAndAdvertType(Math.toIntExact(rewardedTextAd.get().getId()), "text");
            int targetAudienceNumber = rewardedTextAd.get().getTargetAmount() / Integer.parseInt(rewardedTextAd.get().getAmount());
            if ((targetAudienceNumber == rewardedTextAd.get().getTotalViews()) || rewardedTextAd.get().getIsClosed()) {
                rewardedTextAdRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
                rewardedTextAdRepository.save(rewardedTextAd.get());
                response.put("statusCode", "1");
                response.put("message", "Reward closed");
                return ResponseEntity.accepted().body(response);
            }

            TextRewards rewards = rewardedTextsRepository.findByPhoneAndAdvertId(
                    rewardRequest.getPhoneNumber(),
                    Long.parseLong(rewardRequest.getAdvertId())
            );

            if (rewards != null) {
                response.put("message", "User has already been rewarded");
                response.put("statusCode", "2");
                rewards.setViews(rewards.getViews() + 1);
                rewardedTextAdRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
                rewardedTextsRepository.save(rewards);

                return ResponseEntity.accepted().body(response);
            }

            TextRewards textRewards = new TextRewards();
            if (voucherService.existsByPhoneNumberAndAdvertIdAndAdTitle(
                    rewardRequest.getPhoneNumber(),
                    Long.valueOf(rewardRequest.getAdvertId()),
                    rewardedTextAd.get().getTitle()) ||
                    customVoucherRepository.existsByAdvertIdAndAdvertTypeAndClientPhoneNumber(
                            Math.toIntExact(rewardedTextAd.get().getId()), "text", rewardRequest.getPhoneNumber()
                    )
            ) {
                response.put("message", "User has already been rewarded");
                response.put("statusCode", "2");
                return ResponseEntity.accepted().body(response);
            }


            Integer totalViews = rewardedTextAd.get().getTotalViews() + 1;
            log.info(totalViews.toString());
            rewardedTextAd.get().setTotalViews(totalViews);
            log.info("william");

            // return ResponseEntity.accepted().body(response);
            rewardedTextAdRepository.save(rewardedTextAd.get());
            if (!customVouchers.isEmpty())
            {
                CustomVoucher customVoucher = new CustomVoucher();
                if (customVouchers.size() == 1) {
                    customVoucher = customVouchers.get(0);
                    if (customVoucher.getClientPhoneNumber().contains(rewardRequest.getPhoneNumber())) {
                        response.put("message", "User has already been rewarded");
                        response.put("statusCode", "2");
                        return ResponseEntity.accepted().body(response);
                    }
                }
                else {
                    List<CustomVoucher> cVs = customVoucherRepository
                            .findCustomVouchersByAdvertIdAndAdvertTypeAndRedeemed(Math.toIntExact(rewardedTextAd.get().getId()), "text", false);
                    customVoucher = cVs.get(0);
                }
                customVoucher.setClientGender(rewardRequest.getGender());
                customVoucher.setClientLocation(rewardRequest.getLocation());
                customVoucher.setRedeemed(true);
                customVoucher.setClientPhoneNumber(rewardRequest.getPhoneNumber());
                customVoucher.setDateSentOut(LocalDateTime.now());
                customVoucher.setClientName(customer.getFirstName() + " " + customer.getLastName());
                customVoucherRepository.save(customVoucher);

                // Set Voucher to vouchers table
                // Voucher voucher = new Voucher();
                voucher.setCode(customVoucher.getVoucherCode());
                voucher.setValue(rewardRequest.getAmount());
                voucher.setVoucherValid(true);
                voucher.setCreatedAt(LocalDateTime.now());
                // voucher.setThumbnail(rewardedTextAd.get().getThumbnail());
                voucher.setCompany(rewardedTextAd.get().getCompany());
                voucher.setExpirationDate(rewardedTextAd.get().getVoucherValidityEndDate().toLocalDate().atStartOfDay());
                voucher.setAdType(rewardRequest.getAdType());
                voucher.setSystemGenerated(false);

                voucher.setAdvertId(Long.parseLong(rewardRequest.getAdvertId()));
                voucher.setAdTitle(rewardedTextAd.get().getTitle());
                voucher.setPhoneNumber(rewardRequest.getPhoneNumber());
                Voucher savedVoucher = vouchersRepository.save(voucher);

                // Set Rewarded Texts answers
                textRewards.setPhone(rewardRequest.getPhoneNumber());
                textRewards.setAmount(rewardRequest.getAmount());
                textRewards.setAdvertType(rewardRequest.getAdType());
                textRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
                textRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
                textRewards.setViews(1);
                textRewards.setLocation(rewardRequest.getLocation());
                textRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
                textRewards.setAdvertTitle(rewardedTextAd.get().getTitle());
                textRewards.setGender(rewardRequest.getGender());
                textRewards.setResultCode(0);
                rewardedTextsRepository.save(textRewards);
                /* advantaSmsApi.sendSmsNotification(
                        "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                rewardedTextAd.get().getCompany() + " valid till " + rewardedTextAd.get().getVoucherValidityEndDate().toString().split("T")[0]
                                +". Click pilot.tam.co.ke?v=" + customVouchers.get(0).getVoucherCode() + "&c=t&a=" + rewardedTextAd.get().getId()
                                + "&d=" + savedVoucher.getId() + " to redeem.",
                        rewardRequest.getPhoneNumber()
                );*/
            }
            else {

                // Generate Coupon and send sms notification to user. Update the amount_paid_out column
                voucher = voucherService.generateVoucher(
                        rewardRequest.getAmount(),
                        rewardedTextAd.get().getCompany(),
                        rewardRequest.getPhoneNumber(),
                        rewardedTextAd.get().getBrand(),
                        rewardedTextAd.get().getTitle(),
                        rewardRequest.getAdType(),
                        Long.valueOf(rewardRequest.getAdvertId()),
                        rewardedTextAd.get().getBrandLogo()
                );
                // Update the total Views column by adding 1 view
                log.info("William 3:::");
                // rewardedTextAdRepository.updateAmountPaidOut(Long.parseLong(rewardRequest.getAdvertId()), rewardRequest.getAmount());
                // save the user reward and ad details in rewarded_texts table
                textRewards.setPhone(rewardRequest.getPhoneNumber());
                textRewards.setAmount(rewardRequest.getAmount());
                textRewards.setAdvertType(rewardRequest.getAdType());
                textRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
                textRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
                textRewards.setViews(1);
                textRewards.setLocation(rewardRequest.getLocation());
                textRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
                textRewards.setGender(rewardRequest.getGender());
                textRewards.setAdvertTitle(rewardedTextAd.get().getTitle());
                textRewards.setResultCode(0);
                rewardedTextsRepository.save(textRewards);

                /* advantaSmsApi.sendSmsNotification(
                        "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                rewardedTextAd.get().getCompany() + " valid till " + rewardedTextAd.get().getVoucherValidityEndDate().toString().split("T")[0]
                                +". Click pilot.tam.co.ke?v=" + voucher.getCode() + "&c=t&a=" + rewardedTextAd.get().getId()
                                + "&d=" + voucher.getId() + " to redeem.",
                        rewardRequest.getPhoneNumber()
                );*/
            }
            if (rewardedTextAd.get().getIndustry().startsWith("Gaming")) {
                sendGamingVoucherMessage(customer.getFirstName(), rewardedTextAd.get().getCompany(),rewardedTextAd.get().getVoucherValidityEndDate().toString().split("T")[0],
                        rewardedTextAd.get().getCallToActionUrl(), rewardRequest.getPhoneNumber(), rewardedTextAd.get().getRewardText());
            } else {
                sendVoucherMessage(customer.getFirstName(), rewardRequest.getAmount(), rewardedTextAd.get().getCompany(),
                        rewardedTextAd.get().getVoucherValidityEndDate().toString().split("T")[0],
                        voucher.getCode(), rewardedTextAd.get().getId(), voucher.getId(),
                        rewardRequest.getPhoneNumber(), "t", rewardedTextAd.get().getRewardText());
            }
//            sendVoucherMessage(customer.getFirstName(), rewardRequest.getAmount(), rewardedTextAd.get().getCompany(),
//                    rewardedTextAd.get().getVoucherValidityEndDate().toString().split("T")[0],
//                    voucher.getCode(), rewardedTextAd.get().getId(), voucher.getId(),
//                    rewardRequest.getPhoneNumber(), "t", rewardedTextAd.get().getRewardText());
            response.put("message", "Reward has been sent");
            response.put("statusCode", "0");
            rewardUser("Text", rewardedTextAd.get().getId());
            return ResponseEntity.accepted().body(response);
        }
        else if (Objects.equals(rewardRequest.getAdType(), "video")) {
            Optional<RewardedVideoAds> rewardedVideoAds = rewardedVideoAdRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );
            List<CustomVoucher> customVouchers = customVoucherRepository.findCustomVouchersByAdvertIdAndAdvertType(Math.toIntExact(rewardedVideoAds.get().getId()), "video");
//            if (customVouchers.isEmpty()) {
//                response.put("statusCode", "1");
//                response.put("message", "No vouchers available");
//                return ResponseEntity.accepted().body(response);
//            }
            int targetAudienceNumber = rewardedVideoAds.get().getTargetAmount() / Integer.parseInt(rewardedVideoAds.get().getAmount());
            if ((targetAudienceNumber == rewardedVideoAds.get().getTotalViews()) || rewardedVideoAds.get().getIsClosed()) {
                rewardedVideoAdRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
                rewardedVideoAdRepository.save(rewardedVideoAds.get());
                response.put("statusCode", "1");
                response.put("message", "Reward closed");
                return ResponseEntity.accepted().body(response);
            }

            VideoRewards rewards = rewardedVideoRepository.findByPhoneAndAdvertId(
                    rewardRequest.getPhoneNumber(),
                    Long.parseLong(rewardRequest.getAdvertId())
            );

            if (rewards != null) {
                response.put("message", "User has already been rewarded");
                response.put("statusCode", "2");
                rewards.setViews(rewards.getViews() + 1);
                rewardedVideoAdRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
                rewardedVideoRepository.save(rewards);

                return ResponseEntity.accepted().body(response);
            }

            VideoRewards videoRewards = new VideoRewards();
            if (voucherService.existsByPhoneNumberAndAdvertIdAndAdTitle(
                    rewardRequest.getPhoneNumber(),
                    Long.valueOf(rewardRequest.getAdvertId()),
                    rewardedVideoAds.get().getTitle()) ||
                    customVoucherRepository.existsByAdvertIdAndAdvertTypeAndClientPhoneNumber(
                            Math.toIntExact(rewardedVideoAds.get().getId()), "video", rewardRequest.getPhoneNumber()
                    )
            ) {
                response.put("message", "User has already been rewarded");
                response.put("statusCode", "2");
                return ResponseEntity.accepted().body(response);
            }

            int totalViews = rewardedVideoAds.get().getTotalViews() + 1;
            rewardedVideoAds.get().setTotalViews(totalViews);
            rewardedVideoAdRepository.save(rewardedVideoAds.get());
            log.info("william");
            log.info("Custom Vouchers {}", customVouchers);
            log.info("Custom Vouchers {}", customVouchers.size());
            if (!customVouchers.isEmpty())
            {
                CustomVoucher customVoucher = new CustomVoucher();
                if (customVouchers.size() == 1) {
                    customVoucher = customVouchers.get(0);
                    if (customVoucher.getClientPhoneNumber().contains(rewardRequest.getPhoneNumber())) {
                        response.put("message", "User has already been rewarded");
                        response.put("statusCode", "2");
                        return ResponseEntity.accepted().body(response);
                    }
                }
                else {
                    List<CustomVoucher> cVs = customVoucherRepository
                            .findCustomVouchersByAdvertIdAndAdvertTypeAndRedeemed(Math.toIntExact(rewardedVideoAds.get().getId()), "video", false);
                    customVoucher = cVs.get(0);
                }
                customVoucher.setClientGender(rewardRequest.getGender());
                customVoucher.setClientLocation(rewardRequest.getLocation());
                customVoucher.setRedeemed(true);
                customVoucher.setClientPhoneNumber(rewardRequest.getPhoneNumber());
                customVoucher.setDateSentOut(LocalDateTime.now());
                customVoucher.setClientName(customer.getFirstName() + " " + customer.getLastName());
                customVoucherRepository.save(customVoucher);

                // Set Voucher to vouchers table
                // voucher = new Voucher();
                voucher.setCode(customVoucher.getVoucherCode());
                voucher.setValue(rewardRequest.getAmount());
                voucher.setVoucherValid(true);
                voucher.setCreatedAt(LocalDateTime.now());
                // voucher.setThumbnail(rewardedVideoAds.get().getImage());
                voucher.setCompany(rewardedVideoAds.get().getCompany());
                voucher.setExpirationDate(rewardedVideoAds.get().getVoucherValidityEndDate().toLocalDate().atStartOfDay());
                voucher.setAdType(rewardRequest.getAdType());
                voucher.setSystemGenerated(false);

                voucher.setAdvertId(Long.parseLong(rewardRequest.getAdvertId()));
                voucher.setAdTitle(rewardedVideoAds.get().getTitle());
                voucher.setPhoneNumber(rewardRequest.getPhoneNumber());
                Voucher savedVoucher = vouchersRepository.save(voucher);

                // Set Rewarded Texts answers
                videoRewards.setPhone(rewardRequest.getPhoneNumber());
                videoRewards.setAmount(rewardRequest.getAmount());
                videoRewards.setAdvertType(rewardRequest.getAdType());
                videoRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
                videoRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
                videoRewards.setViews(1);
                videoRewards.setLocation(rewardRequest.getLocation());
                videoRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
                videoRewards.setAdvertTitle(rewardedVideoAds.get().getTitle());
                videoRewards.setGender(rewardRequest.getGender());
                videoRewards.setResultCode(0);
                rewardedVideoRepository.save(videoRewards);

/*                advantaSmsApi.sendSmsNotification(
                        "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                rewardedVideoAds.get().getCompany() + " valid till " + rewardedVideoAds.get().getVoucherValidityEndDate().toString().split("T")[0]
                                +". Click pilot.tam.co.ke?v=" + customVouchers.get(0).getVoucherCode() + "&c=v&a=" + rewardedVideoAds.get().getId()
                                + "&d=" + savedVoucher.getId() + " to redeem.",
                        rewardRequest.getPhoneNumber()
                );*/
            }
            else {
                log.info("William 3:::");
                // Generate Coupon and send sms notification to user. Update the amount_paid_out column
                voucher = voucherService.generateVoucher(
                        rewardRequest.getAmount(),
                        rewardedVideoAds.get().getCompany(),
                        rewardRequest.getPhoneNumber(),
                        rewardedVideoAds.get().getBrand(),
                        rewardedVideoAds.get().getTitle(),
                        rewardRequest.getAdType(),
                        Long.valueOf(rewardRequest.getAdvertId()),
                        rewardedVideoAds.get().getBrandLogo()
                );
                // Update the total Views column by adding 1 view

                // rewardedTextAdRepository.updateAmountPaidOut(Long.parseLong(rewardRequest.getAdvertId()), rewardRequest.getAmount());
                // save the user reward and ad details in rewarded_texts table
                videoRewards.setPhone(rewardRequest.getPhoneNumber());
                videoRewards.setAmount(rewardRequest.getAmount());
                videoRewards.setAdvertType(rewardRequest.getAdType());
                videoRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
                videoRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
                videoRewards.setViews(1);
                videoRewards.setLocation(rewardRequest.getLocation());
                videoRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
                videoRewards.setGender(rewardRequest.getGender());
                videoRewards.setAdvertTitle(rewardedVideoAds.get().getTitle());
                videoRewards.setResultCode(0);
                rewardedVideoRepository.save(videoRewards);

/*                advantaSmsApi.sendSmsNotification(
                        "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                rewardedVideoAds.get().getCompany() + " valid till " + rewardedVideoAds.get().getVoucherValidityEndDate().toString().split("T")[0]
                                +". Click pilot.tam.co.ke?v=" + voucher.getCode() + "&c=v&a=" + rewardedVideoAds.get().getId()
                                + "&d=" + voucher.getId() + " to redeem.",
                        rewardRequest.getPhoneNumber()
                );*/
            }

            if (rewardedVideoAds.get().getIndustry().startsWith("Gaming")) {
                sendGamingVoucherMessage(customer.getFirstName(), rewardedVideoAds.get().getCompany(),rewardedVideoAds.get().getVoucherValidityEndDate().toString().split("T")[0],
                        rewardedVideoAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber(), rewardedVideoAds.get().getRewardText());
            } else {
                sendVoucherMessage(customer.getFirstName(), rewardRequest.getAmount(), rewardedVideoAds.get().getCompany(),
                        rewardedVideoAds.get().getVoucherValidityEndDate().toString().split("T")[0],
                        voucher.getCode(), rewardedVideoAds.get().getId(), voucher.getId(),
                        rewardRequest.getPhoneNumber(), "v", rewardedVideoAds.get().getRewardText());
            }
//            sendVoucherMessage(customer.getFirstName(), rewardRequest.getAmount(), rewardedVideoAds.get().getCompany(),
//                    rewardedVideoAds.get().getVoucherValidityEndDate().toString().split("T")[0],
//                    voucher.getCode(), rewardedVideoAds.get().getId(), voucher.getId(),
//                    rewardRequest.getPhoneNumber(), "v", rewardedVideoAds.get().getRewardText());
            response.put("message", "Reward has been sent");
            response.put("statusCode", "0");
            rewardUser("Text", rewardedVideoAds.get().getId());
            return ResponseEntity.accepted().body(response);

        }
        else if (Objects.equals(rewardRequest.getAdType(), "audio")) {
            Optional<RewardedAudioAds> rewardedAudioAds = rewardedAudioAdsRepository.findById(
                    Long.parseLong(rewardRequest.getAdvertId())
            );
            List<CustomVoucher> customVouchers = customVoucherRepository.findCustomVouchersByAdvertIdAndAdvertType(Math.toIntExact(rewardedAudioAds.get().getId()), "audio");
            log.info("William 3::: Audio");
            int targetAudienceNumber = rewardedAudioAds.get().getTargetAmount() / Integer.parseInt(rewardedAudioAds.get().getAmount());
            if ((targetAudienceNumber == rewardedAudioAds.get().getTotalViews()) || rewardedAudioAds.get().getIsClosed()) {
                rewardedAudioAdsRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
                rewardedAudioAdsRepository.save(rewardedAudioAds.get());
                response.put("statusCode", "1");
                response.put("message", "Reward closed");
                return ResponseEntity.accepted().body(response);
            }

            AudioRewards rewards = rewardedAudioRepository.findByPhoneAndAdvertId(
                    rewardRequest.getPhoneNumber(),
                    Long.parseLong(rewardRequest.getAdvertId())
            );

            if (rewards != null) {
                response.put("message", "User has already been rewarded");
                response.put("statusCode", "2");
                rewards.setViews(rewards.getViews() + 1);
                rewardedAudioAdsRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
                rewardedAudioRepository.save(rewards);

                return ResponseEntity.accepted().body(response);
            }

            AudioRewards audioRewards = new AudioRewards();
            if (voucherService.existsByPhoneNumberAndAdvertIdAndAdTitle(
                    rewardRequest.getPhoneNumber(),
                    Long.valueOf(rewardRequest.getAdvertId()),
                    rewardedAudioAds.get().getTitle()) ||
                    customVoucherRepository.existsByAdvertIdAndAdvertTypeAndClientPhoneNumber(
                            Math.toIntExact(rewardedAudioAds.get().getId()), "audio", rewardRequest.getPhoneNumber()
                    )
            ) {
                log.info("Steven 1:::");
                response.put("message", "User has already been rewarded");
                response.put("statusCode", "2");
                return ResponseEntity.accepted().body(response);
            }

            int totalViews = rewardedAudioAds.get().getTotalViews() + 1;
            rewardedAudioAds.get().setTotalViews(totalViews);
            rewardedAudioAdsRepository.save(rewardedAudioAds.get());
            if (!customVouchers.isEmpty())
            {

                CustomVoucher customVoucher = new CustomVoucher();
                if (customVouchers.size() == 1) {
                    customVoucher = customVouchers.get(0);
                    if (customVoucher.getClientPhoneNumber().contains(rewardRequest.getPhoneNumber())) {
                        response.put("message", "User has already been rewarded");
                        response.put("statusCode", "2");
                        return ResponseEntity.accepted().body(response);
                    }
                }
                else {
                    List<CustomVoucher> cVs = customVoucherRepository
                            .findCustomVouchersByAdvertIdAndAdvertTypeAndRedeemed(Math.toIntExact(rewardedAudioAds.get().getId()), "audio", false);
                    customVoucher = cVs.get(0);
                }
                customVoucher.setClientGender(rewardRequest.getGender());
                customVoucher.setClientLocation(rewardRequest.getLocation());
                customVoucher.setRedeemed(true);
                customVoucher.setClientPhoneNumber(rewardRequest.getPhoneNumber());
                customVoucher.setDateSentOut(LocalDateTime.now());
                customVoucher.setClientName(customer.getFirstName() + " " + customer.getLastName());


                // Set Voucher to vouchers table
                // voucher = new Voucher();
                voucher.setCode(customVoucher.getVoucherCode());
                voucher.setValue(rewardRequest.getAmount());
                voucher.setVoucherValid(true);
                voucher.setCreatedAt(LocalDateTime.now());
                voucher.setCompany(rewardedAudioAds.get().getCompany());
                voucher.setExpirationDate(rewardedAudioAds.get().getVoucherValidityEndDate().toLocalDate().atStartOfDay());
                voucher.setAdType(rewardRequest.getAdType());
                voucher.setSystemGenerated(false);

                voucher.setAdvertId(Long.parseLong(rewardRequest.getAdvertId()));
                voucher.setAdTitle(rewardedAudioAds.get().getTitle());
                voucher.setPhoneNumber(rewardRequest.getPhoneNumber());



                // Set Rewarded Texts answers
                audioRewards.setPhone(rewardRequest.getPhoneNumber());
                audioRewards.setAmount(rewardRequest.getAmount());
                audioRewards.setAdvertType(rewardRequest.getAdType());
                audioRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
                audioRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
                audioRewards.setViews(1);
                audioRewards.setLocation(rewardRequest.getLocation());
                audioRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
                audioRewards.setAdvertTitle(rewardedAudioAds.get().getTitle());
                audioRewards.setGender(rewardRequest.getGender());
                audioRewards.setResultCode(0);

                rewardedAudioRepository.save(audioRewards);
                voucher = vouchersRepository.save(voucher);
                customVoucherRepository.save(customVoucher);
/*                advantaSmsApi.sendSmsNotification(
                        "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                rewardedAudioAds.get().getCompany() + " valid till " + rewardedAudioAds.get().getVoucherValidityEndDate().toString().split("T")[0]
                                +". Click pilot.tam.co.ke?v=" + customVouchers.get(0).getVoucherCode() + "&c=a&a=" + rewardedAudioAds.get().getId()
                                + "&d=" + savedVoucher.getId() + " to redeem.",
                        rewardRequest.getPhoneNumber()
                );*/
            }
            else {
                // Generate Coupon and send sms notification to user. Update the amount_paid_out column
                voucher = voucherService.generateVoucher(
                        rewardRequest.getAmount(),
                        rewardedAudioAds.get().getCompany(),
                        rewardRequest.getPhoneNumber(),
                        rewardedAudioAds.get().getBrand(),
                        rewardedAudioAds.get().getTitle(),
                        rewardRequest.getAdType(),
                        Long.valueOf(rewardRequest.getAdvertId()),
                        rewardedAudioAds.get().getBrandLogo()
                );
                // Update the total Views column by adding 1 view

                // rewardedTextAdRepository.updateAmountPaidOut(Long.parseLong(rewardRequest.getAdvertId()), rewardRequest.getAmount());
                // save the user reward and ad details in rewarded_texts table
                audioRewards.setPhone(rewardRequest.getPhoneNumber());
                audioRewards.setAmount(rewardRequest.getAmount());
                audioRewards.setAdvertType(rewardRequest.getAdType());
                audioRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
                audioRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
                audioRewards.setViews(1);
                audioRewards.setLocation(rewardRequest.getLocation());
                audioRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
                audioRewards.setGender(rewardRequest.getGender());
                audioRewards.setAdvertTitle(rewardedAudioAds.get().getTitle());
                audioRewards.setResultCode(0);
                rewardedAudioRepository.save(audioRewards);

/*                advantaSmsApi.sendSmsNotification(
                        "Congrats " + customer.getFirstName() + ", You've received an eVoucher worth Ksh " + rewardRequest.getAmount() + " from " +
                                rewardedAudioAds.get().getCompany() + " valid till " + rewardedAudioAds.get().getVoucherValidityEndDate().toString().split("T")[0]
                                +". Click pilot.tam.co.ke?v=" + voucher.getCode() + "&c=a&a=" + rewardedAudioAds.get().getId()
                                + "&d=" + voucher.getId() + " to redeem.",
                        rewardRequest.getPhoneNumber()
                );*/
/*                sendMessage(customer.getFirstName(), rewardRequest.getAmount(), rewardedAudioAds.get().getCompany(), rewardedAudioAds.get().getVoucherValidityEndDate().toString().split("T")[0],
                        voucher.getCode(), Long.parseLong(rewardRequest.getAdvertId()), voucher.getId(), rewardedAudioAds.get().getDescription(),
                        rewardRequest.getPhoneNumber());*/
            }
            if (rewardedAudioAds.get().getIndustry().startsWith("Gaming")) {
                sendGamingVoucherMessage(customer.getFirstName(), rewardedAudioAds.get().getCompany(),rewardedAudioAds.get().getVoucherValidityEndDate().toString().split("T")[0],
                        rewardedAudioAds.get().getCallToActionUrl(), rewardRequest.getPhoneNumber(), rewardedAudioAds.get().getRewardText());
            } else {
                sendVoucherMessage(customer.getFirstName(), rewardRequest.getAmount(), rewardedAudioAds.get().getCompany(),
                        rewardedAudioAds.get().getVoucherValidityEndDate().toString().split("T")[0],
                        voucher.getCode(), rewardedAudioAds.get().getId(), voucher.getId(),
                        rewardRequest.getPhoneNumber(), "a", rewardedAudioAds.get().getRewardText());
            }

            response.put("message", "Reward has been sent");
            response.put("statusCode", "0");
            rewardUser("Text", rewardedAudioAds.get().getId());
            return ResponseEntity.accepted().body(response);

        }

        return ResponseEntity.accepted().body(response);
    }

    void sendAirtimeMessage(String firstName, BigDecimal amount, String company, String url, String phoneNumber) {
        advantaSmsApi.sendSmsNotification(
                "Congrats " + firstName + ", You've received an " + "airtime" + " reward of Ksh " + amount + " from " +
                        company + ". Click to earn more " + url + ".",
                phoneNumber
        );
    }

    void sendCashMessage(String firstName, BigDecimal amount, String company, String url, String phoneNumber) {
        advantaSmsApi.sendSmsNotification(
                "Congrats " + firstName + ", You've received a " + "cash" + " reward of Ksh " + amount + " from " +
                        company + ". Click to earn more " + url + ".",
                phoneNumber
        );
    }


    void sendMessage(String firstName, BigDecimal amount, String company, String voucherValidityEndDate, String voucherCode, Long adID, Long voucherID, String discount_off, String phoneNumber, String adType, String reward_text) {
        if (discount_off.contains("Ksh")) {

            advantaSmsApi.sendSmsNotification(
                    "Congrats " + firstName + ", Get " + reward_text + " from " +
                            company + " valid till " + voucherValidityEndDate
                            +". Click tam.co.ke?v=" + voucherCode + "&c=" + adType + "&a=" + adID
                            + "&d=" + voucherID + " to redeem.",
                    phoneNumber
            );
            return;
        }
        advantaSmsApi.sendSmsNotification(
                "Congrats " + firstName + ", You've received an eVoucher for " + discount_off + " from " +
                        company + " valid till " + voucherValidityEndDate
                        +". Click tam.co.ke?v=" + voucherCode + "&c=" + adType + "&a=" + adID
                        + "&d=" + voucherID + " to redeem.",
                phoneNumber
        );
    }

    void sendGamingVoucherMessage(String firstName, String company, String voucherValidityEndDate, String callToActionUrl, String phoneNumber, String reward_text) {
        advantaSmsApi.sendSmsNotification(
                "Congrats " + firstName + ", Get " + reward_text + " from " +
                        company + " valid till " + voucherValidityEndDate
                        +". Click " + callToActionUrl +  " to redeem.",
                phoneNumber
        );
    }

    void sendVoucherMessage(String firstName, BigDecimal amount, String company, String voucherValidityEndDate, String voucherCode, Long adID, Long voucherID, String phoneNumber, String adType, String reward_text) {
        advantaSmsApi.sendSmsNotification(
                "Congrats " + firstName + ", Get " + reward_text + " from " +
                        company + " valid till " + voucherValidityEndDate
                        +". Click tam.co.ke?v=" + voucherCode + "&c=" + adType + "&a=" + adID
                        + "&d=" + voucherID + " to redeem.",
                phoneNumber
        );
    }

    public void rewardUser(String adType, Long adID) {
        publishUser.publishMessage("RewardSuccess",adType + "," + adID);
    }

    public Map<String, String> checkAndSaveImageRewards(Optional<RewardedTextAd> rewardedTextAd,
                                                        RewardRequest rewardRequest,
                                                        Map<String, String> response) {
        int targetAudienceNumber = rewardedTextAd.get().getTargetAmount() / Integer.parseInt(rewardedTextAd.get().getAmount());
        if ((targetAudienceNumber == rewardedTextAd.get().getTotalViews()) || rewardedTextAd.get().getIsClosed()) {
            rewardedTextAdRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
            rewardedTextAdRepository.save(rewardedTextAd.get());
            response.put("statusCode", "1");
            response.put("message", "Reward closed");
            return response;
        }

        TextRewards rewards = rewardedTextsRepository.findByPhoneAndAdvertId(
                rewardRequest.getPhoneNumber(),
                Long.parseLong(rewardRequest.getAdvertId())
        );

        if (rewards != null) {
            response.put("message", "User has already been rewarded");
            response.put("statusCode", "2");
            rewards.setViews(rewards.getViews() + 1);
            rewardedTextsRepository.save(rewards);
            // return ResponseEntity.accepted().body(response);
            return response;
        }
        TextRewards textRewards = new TextRewards();
        int totalViews = rewardedTextAd.get().getTotalViews() + 1;
        int amountPaidOut = totalViews * Integer.parseInt(rewardedTextAd.get().getAmount());
        rewardedTextAd.get().setTotalViews(totalViews);
        // rewardedTextAd.get().setAmountPaidOut((long) amountPaidOut);
        rewardedTextAdRepository.save(rewardedTextAd.get());
        // rewardedTextAdRepository.updateAmountPaidOut(Long.parseLong(rewardRequest.getAdvertId()), rewardRequest.getAmount());
        // log.info("User has not been rewarded. send ad reward");
        textRewards.setPhone(rewardRequest.getPhoneNumber());
        textRewards.setAmount(rewardRequest.getAmount());
        textRewards.setAdvertType(rewardRequest.getAdType());
        textRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
        textRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
        textRewards.setViews(1);
        textRewards.setLocation(rewardRequest.getLocation());
        textRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
        textRewards.setAdvertTitle(rewardedTextAd.get().getTitle());
        textRewards.setGender(rewardRequest.getGender());
        textRewards.setResultCode(0);
        rewardedTextsRepository.save(textRewards);
        response.put("statusCode", "0");
        // return ResponseEntity.accepted().body(response);
        return response;
    }


    public Map<String, String> checkAndSaveVideoRewards(Optional<RewardedVideoAds> rewardedVideoAds,
                                                        RewardRequest rewardRequest,
                                                        Map<String, String> response) {
        int targetAudienceNumber = rewardedVideoAds.get().getTargetAmount() / Integer.parseInt(rewardedVideoAds.get().getAmount());
        if ((targetAudienceNumber == rewardedVideoAds.get().getTotalViews()) || rewardedVideoAds.get().getIsClosed()) {
            rewardedVideoAdRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
            rewardedVideoAdRepository.save(rewardedVideoAds.get());
            response.put("statusCode", "1");
            response.put("message", "Reward closed");
            return response;
        }

        // check if user has been rewarded already or not/
        VideoRewards rewards = rewardedVideoRepository.findByPhoneAndAdvertId(
                rewardRequest.getPhoneNumber(),
                Long.parseLong(rewardRequest.getAdvertId())
        );

        if (rewards != null) {
            response.put("message", "User has already been rewarded");
            response.put("statusCode", "2");
            rewards.setViews(rewards.getViews() + 1);
            rewardedVideoRepository.save(rewards);
            return response;
        }
        VideoRewards videoRewards = new VideoRewards();
        int totalViews = rewardedVideoAds.get().getTotalViews() + 1;
        rewardedVideoAds.get().setTotalViews(totalViews);
        rewardedVideoAdRepository.save(rewardedVideoAds.get());
        log.info("User has not been rewarded. send ad reward");
        videoRewards.setPhone(rewardRequest.getPhoneNumber());
        videoRewards.setAmount(rewardRequest.getAmount());
        videoRewards.setAdvertType(rewardRequest.getAdType());
        videoRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
        videoRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
        videoRewards.setViews(1);
        videoRewards.setLocation(rewardRequest.getLocation());
        videoRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
        videoRewards.setAdvertTitle(rewardedVideoAds.get().getTitle());
        videoRewards.setGender(rewardRequest.getGender());
        videoRewards.setResultCode(0);
        rewardedVideoRepository.save(videoRewards);
        response.put("statusCode", "0");
        // return ResponseEntity.accepted().body(response);
        return response;
    }


    public Map<String, String> checkAndSaveAudioRewards(Optional<RewardedAudioAds> rewardedAudioAds,
                                                        RewardRequest rewardRequest,
                                                        Map<String, String> response) {
        int targetAudienceNumber = rewardedAudioAds.get().getTargetAmount() / Integer.parseInt(rewardedAudioAds.get().getAmount());
        if ((targetAudienceNumber == rewardedAudioAds.get().getTotalViews()) || rewardedAudioAds.get().getIsClosed()) {
            rewardedAudioAdsRepository.closeAd(Long.parseLong(rewardRequest.getAdvertId()));
            rewardedAudioAdsRepository.save(rewardedAudioAds.get());
            response.put("statusCode", "1");
            response.put("message", "Reward closed");
            return response;
        }

        // check if user has been rewarded already or not/
        AudioRewards rewards = rewardedAudioRepository.findByPhoneAndAdvertId(
                rewardRequest.getPhoneNumber(),
                Long.parseLong(rewardRequest.getAdvertId())
        );

        if (rewards != null) {
            response.put("message", "User has already been rewarded");
            response.put("statusCode", "2");
            rewards.setViews(rewards.getViews() + 1);
            rewardedAudioRepository.save(rewards);
            return response;
        }
        AudioRewards audioRewards = new AudioRewards();
        int totalViews = rewardedAudioAds.get().getTotalViews() + 1;
        int amountPaidOut = totalViews * Integer.parseInt(rewardedAudioAds.get().getAmount());
        rewardedAudioAds.get().setTotalViews(totalViews);
        // rewardedAudioAds.get().setAmountPaidOut(String.valueOf(amountPaidOut));
        rewardedAudioAdsRepository.save(rewardedAudioAds.get());
        audioRewards.setPhone(rewardRequest.getPhoneNumber());
        audioRewards.setAmount(rewardRequest.getAmount());
        audioRewards.setAdvertType(rewardRequest.getAdType());
        audioRewards.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
        audioRewards.setOpinionAnswer(rewardRequest.getOpinionAnswer());
        audioRewards.setViews(1);
        audioRewards.setLocation(rewardRequest.getLocation());
        audioRewards.setAge(Integer.valueOf(rewardRequest.getAge()));
        audioRewards.setAdvertTitle(rewardedAudioAds.get().getTitle());
        audioRewards.setGender(rewardRequest.getGender());
        audioRewards.setResultCode(0);
        rewardedAudioRepository.save(audioRewards);
        response.put("statusCode", "0");
        // return ResponseEntity.accepted().body(response);
        return response;
    }


}
