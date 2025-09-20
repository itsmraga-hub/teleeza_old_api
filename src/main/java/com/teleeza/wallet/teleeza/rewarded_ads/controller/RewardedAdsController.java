package com.teleeza.wallet.teleeza.rewarded_ads.controller;

import com.amazonaws.services.rekognition.model.Video;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.MQTT.PublishUser;
import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.common.AcknowledgeResponse;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses.B2CTransactionAsyncResponse;
import com.teleeza.wallet.teleeza.daraja.service.DarajaApi;
import com.teleeza.wallet.teleeza.rateconfig.RateLimitConfig;
import com.teleeza.wallet.teleeza.rewarded_ads.dtos.Request.RewardRequest;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.*;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredAds;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity.Companies;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository.CompaniesRepository;
import com.teleeza.wallet.teleeza.rewarded_ads.repository.*;
import com.teleeza.wallet.teleeza.rewarded_ads.service.RewardService;
import com.teleeza.wallet.teleeza.rewarded_ads.service.RewardedAdsServiceImpl;
import com.teleeza.wallet.teleeza.rewarded_ads.service.VoucherService;
import com.teleeza.wallet.teleeza.rewarded_ads.utils.CompetitiveSeparation;
import com.teleeza.wallet.teleeza.topups.send_airtime.service.IAirtimeService;
import com.teleeza.wallet.teleeza.topups.send_airtime.service.SendAirtimeService;
import com.teleeza.wallet.teleeza.utils.ResponseHandler;
import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("v1/teleeza/rewarded")
@Slf4j
public class RewardedAdsController {
    @Autowired
    private VouchersRepository vouchersRepository;
    @Autowired
    private RewardedVideoAdRepository rewardedVideoAdRepository;

    @Autowired
    private final IAirtimeService tupayAirtimeService;

    @Autowired
    private final TargetGroupsRepository targetGroupsRepository;

    @Autowired
    private final CompaniesRepository companiesRepository;

    @Autowired
    private RewardedAudioAdsRepository rewardedAudioAdsRepository;
    @Autowired
    private RewardedTextAdRepository rewardedTextAdRepository;
    @Autowired
    private SponsoredRewardAdRepository sponsoredRewardAdRepository;
    @Autowired
    private SponsoredAnswersRepository sponsoredAnswersRepository;
    @Autowired
    private RewardedVideoRepository rewardedVideoRepository;
    @Autowired
    private RewardedAudioRepository rewardedAudioRepository;
    @Autowired
    private RewardedAdsTransactionsAttemptsRepository rewardedAdsTransactionsAttemptsRepository;
    @Autowired
    private RewardedAdsTransactionRepository rewardedAdsTransactionRepository;
    @Autowired
    private DarajaApi darajaApi;
    @Autowired
    private RewardedTextsRepository rewardedTextsRepository;
    @Autowired
    private IndustriesRepository industriesRepository;
    @Autowired
    private BulkAdRewardsRepository bulkAdRewardsRepository;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private AdvantaSmsApiImpl advantaSmsApi;
    @Autowired
    private RewardedAdsServiceImpl rewardedAdsService;


    @Autowired
    private final ObjectMapper objectMapper;
    private final AcknowledgeResponse acknowledgeResponse;
    private final SendAirtimeService sendAirtimeService;

    private final PublishUser publishUser;
    private final CustomVoucherRepository customVoucherRepository;

    private final RewardService rewardService;

    private final RateLimiter rateLimitConfig;
    private final Map<String, RateLimiter> clientLimiters = new ConcurrentHashMap<>();

    private RateLimiter getClientRateLimiter(String clientId) {
        return clientLimiters.computeIfAbsent(clientId, key -> RateLimitConfig.createRateLimiter());
    }


    public RewardedAdsController(IAirtimeService tupayAirtimeService, TargetGroupsRepository targetGroupsRepository, CompaniesRepository companiesRepository, ObjectMapper objectMapper, AcknowledgeResponse acknowledgeResponse, SendAirtimeService sendAirtimeService, PublishUser publishUser, CustomVoucherRepository customVoucherRepository, RewardService rewardService) {
        this.tupayAirtimeService = tupayAirtimeService;
        this.targetGroupsRepository = targetGroupsRepository;
        this.companiesRepository = companiesRepository;
        this.objectMapper = objectMapper;
        this.acknowledgeResponse = acknowledgeResponse;
        this.sendAirtimeService = sendAirtimeService;
        this.publishUser = publishUser;
        this.customVoucherRepository = customVoucherRepository;
        this.rewardService = rewardService;
        this.rateLimitConfig = RateLimitConfig.createRateLimiter();;
    }

    @GetMapping(path = "/sponsored-ads")
    public ResponseEntity<Object> getRewardedAds(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phoneNumber") String phoneNumber,
            @RequestParam(value = "industry", required = false) String industry,
            @RequestParam(value = "location", required = false) String loc,
            @RequestParam(value = "age", required = false) String age
    ) {
//        RateLimiter clientLimiter = getClientRateLimiter(userId);
//        if (!clientLimiter.acquirePermission()) {
//            return ResponseEntity.status(429).body("You have exceeded the rate limit");
//        }

        Map<String, Object> response = new HashMap<>();
        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + phoneNumber);

        String userAgeGroup = customer.getAgeGroupNo().toString();
        String userLocation = customer.getLocation().toLowerCase().trim();
        String userGender = customer.getGender().toLowerCase().trim();
        List<SponsoredAds> sponsoredAds = sponsoredRewardAdRepository.getActiveSponsoredRewardAds(phoneNumber);
        log.info("Sponsored Ads : {}", sponsoredAds.size());
        if (!sponsoredAds.isEmpty()) {
            String company = sponsoredAds.get(0).getCompany();
            sponsoredAds = sponsoredAds.stream()
                    .filter(ad -> ad.getCompany().equals(company))
                    .collect(Collectors.toList());
        }
        log.info("Sponsored Ads : {}", sponsoredAds.size());
        log.info("Sponsored Ads : {}", sponsoredAds.get(0));
        log.info("Sponsored Ads : {}", sponsoredAds.get(0).getCompany());
        // Filter By Locations
        sponsoredAds = sponsoredAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        // Filter By Gender
        sponsoredAds = sponsoredAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        List<SponsoredAds> unWatchedSponsoredAds = sponsoredAds.stream()
                .filter(ad -> !ad.getIsWatched())
                .collect(Collectors.toList());
        List<SponsoredAds> watchedSponsoredAds = sponsoredAds.stream()
                .filter(ad -> ad.getIsWatched())
                .collect(Collectors.toList());

        List<SponsoredAds> targetGroupsSponsoredAds = sponsoredAds.stream()
                .filter(SponsoredAds::isTarget_group)
                .collect(Collectors.toList());

        List<SponsoredAds> notTargetGroupsSponsoredAds = sponsoredAds.stream()
                .filter(ad -> !ad.isTarget_group())
                .collect(Collectors.toList());
        List<SponsoredAds> filteredSponsoredTargetGroupsAds = targetGroupsSponsoredAds.stream()
                .filter(ad -> {
                    String target_groups = ad.getTarget_groups();
                    List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    log.info("target_groups : {}", target_groups);
                    log.info("targetGroupNames : {}", targetGroupNames);
                    List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                    log.info("Target Groups : {}", targetGroups);
                    log.info(("Company : {}"), ad.getCompany());
                    log.info("targetGroups : {}", targetGroups.size());
                    return targetGroups.stream()
                            .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                })
                .collect(Collectors.toList());
        List<SponsoredAds> filteredSponsoredAds = CompetitiveSeparation.filterSponsoredAds(notTargetGroupsSponsoredAds, userLocation, userGender, userAgeGroup);
        List<SponsoredAds> finalSponsoredAds = Stream.concat(filteredSponsoredTargetGroupsAds.stream(), filteredSponsoredAds.stream())
                .collect(Collectors.toList());
//        Collections.shuffle(unWatchedSponsoredAds);
//        Collections.shuffle(watchedSponsoredAds);
//        List<SponsoredAds> sponsoredAdsList = Stream.concat(unWatchedSponsoredAds.stream(), watchedSponsoredAds.stream()).collect(Collectors.toList());
        response.put("sponsoredAds", finalSponsoredAds);

        return ResponseHandler.generateResponse(HttpStatus.OK, response);
    }

    @GetMapping(path = "/ads")
    public ResponseEntity<Object> getRewardedAds(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phoneNumber") String phoneNumber,
            @RequestParam(value = "location") String userLocation,
            @RequestParam(value = "gender") String userGender
    ) {
        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + phoneNumber);

        log.info("Customer 0000001: {}", customer.getAgeGroupNo());
        BigDecimal totalEarnedFromVideoAds = rewardedVideoRepository.totalEarnedByUserFromVideoAds(phoneNumber);
        BigDecimal totalEarnedFromAudioAds = rewardedAudioRepository.totalEarnedByUserFromAudioAds(phoneNumber);
        BigDecimal totalEarnedFromTextAds = rewardedTextsRepository.totalEarnedByUserFromTextAds(phoneNumber);

        BigDecimal unwatchedAudioAds = rewardedAudioAdsRepository.totalAmountOfUnwatchedAudioAds(phoneNumber);
        BigDecimal unwatchedVideoAds = rewardedVideoAdRepository.totalAmountOfUnwatchedVideoAds(phoneNumber);
        BigDecimal unwatchedTextAds = rewardedTextAdRepository.totalAmountOfUnwatchedTextAds(phoneNumber);

        BigDecimal totalEarnedFromSponsoredVideos = sponsoredAnswersRepository.totalEarnedByUserFromSponsoredAds(phoneNumber);

        BigDecimal unwatchedSponsoredAds = sponsoredRewardAdRepository.totalAmountOfUnwatchedTextAds(phoneNumber);
        BigDecimal moreToEarn = Stream.of(unwatchedVideoAds, unwatchedAudioAds, unwatchedTextAds, unwatchedSponsoredAds)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEarned = Stream.of(totalEarnedFromVideoAds, totalEarnedFromAudioAds, totalEarnedFromTextAds, totalEarnedFromSponsoredVideos)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        BigDecimal amountToBeEarned = totalToActive
//                .subtract(totalEarned);

        log.info("Total earned :{}", totalEarned);
        log.info("More to  earn :{}", moreToEarn);

        List<VideoAds> videoAds = rewardedVideoAdRepository.getActiveVideoAdsOnly(phoneNumber);
        List<AudioAds> audioAds = rewardedAudioAdsRepository.getActiveAudioAdsOnly(phoneNumber);
        List<TextAds> textAds = rewardedTextAdRepository.getRecentFiveAds(phoneNumber);
        List<SponsoredAds> sponsoredAds = sponsoredRewardAdRepository.getActiveSponsoredRewardAds(phoneNumber);

        if (sponsoredAds.size() > 0 ) {
            String company = sponsoredAds.get(0).getCompany();
            sponsoredAds = sponsoredAds.stream()
                    .filter(ad -> ad.getCompany().equals(company))
                    .collect(Collectors.toList());
        }

        log.info("videoAds: {}", videoAds);
        // Separate ads into target group ads and non-target group ads
        List<VideoAds> targetGroupsVideoAds = videoAds.stream()
                .filter(VideoAds::isTarget_group)
                .collect(Collectors.toList());

        List<VideoAds> notTargetGroupsVideoAds = videoAds.stream()
                .filter(ad -> !ad.isTarget_group())
                .collect(Collectors.toList());

        List<AudioAds> targetGroupsAudioAds = audioAds.stream()
                .filter(AudioAds::isTarget_group)
                .collect(Collectors.toList());

        List<AudioAds> notTargetGroupsAudioAds = audioAds.stream()
                .filter(ad -> !ad.isTarget_group())
                .collect(Collectors.toList());

        List<TextAds> targetGroupsTextAds = textAds.stream()
                .filter(TextAds::isTarget_group)
                .collect(Collectors.toList());

        List<TextAds> notTargetGroupsTextAds = textAds.stream()
                .filter(ad -> !ad.isTarget_group())
                .collect(Collectors.toList());

        List<SponsoredAds> targetGroupsSponsoredAds = sponsoredAds.stream()
                .filter(SponsoredAds::isTarget_group)
                .collect(Collectors.toList());

        List<SponsoredAds> notTargetGroupsSponsoredAds = sponsoredAds.stream()
                .filter(ad -> !ad.isTarget_group())
                .collect(Collectors.toList());

        log.info("targetGroupsTextAds: {}", targetGroupsTextAds);
        log.info("notTargetGroupsTextAds: {}", notTargetGroupsTextAds);
// Filter ads for target groups dynamically by company and phone number
        List<VideoAds> filteredVideoTargetGroupsAds = targetGroupsVideoAds.stream()
                .filter(ad -> {
                    String target_groups = ad.getTarget_groups();
                    List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                    log.info("Target Groups : {}", targetGroups);
                    // List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyName(ad.getCompany());
                    log.info(("Company : {}"), ad.getCompany());
                    log.info("targetGroups : {}", targetGroups.size());
                    return targetGroups.stream()
                            .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                })
                .collect(Collectors.toList());

// Combine filtered target group ads and non-target group ads


        List<AudioAds> filteredAudioTargetGroupsAds = targetGroupsAudioAds.stream()
                .filter(ad -> {
                    String target_groups = ad.getTarget_groups();
                    List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                    log.info("Target Groups : {}", targetGroups);
                    // List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyName(ad.getCompany());
                    log.info(("Company : {}"), ad.getCompany());
                    log.info("targetGroups : {}", targetGroups.size());
                    return targetGroups.stream()
                            .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                })
                .collect(Collectors.toList());


        List<TextAds> filteredTextTargetGroupsAds = targetGroupsTextAds.stream()
                .filter(ad -> {
                    String target_groups = ad.getTarget_groups();
                    List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    log.info("target_groups : {}", target_groups);
                    log.info("targetGroupNames : {}", targetGroupNames);
                    List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                    log.info("Target Groups : {}", targetGroups);
                    log.info(("Company : {}"), ad.getCompany());
                    log.info("targetGroups : {}", targetGroups.size());
                    return targetGroups.stream()
                            .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                })
                .collect(Collectors.toList());

        log.info("filteredTextTargetGroupsAds: {}", filteredTextTargetGroupsAds);
        List<SponsoredAds> filteredSponsoredTargetGroupsAds = targetGroupsSponsoredAds.stream()
                .filter(ad -> {
                    String target_groups = ad.getTarget_groups();
                    List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                    log.info("Target Groups : {}", targetGroups);
                    log.info(("Company : {}"), ad.getCompany());
                    log.info("targetGroups : {}", targetGroups.size());
                    return targetGroups.stream()
                            .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                })
                .collect(Collectors.toList());

        // Filter By Locations
        List<VideoAds> locationFilteredVideoAds = notTargetGroupsVideoAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                        .collect(Collectors.toList());

        List<AudioAds> locationFilteredAudioAds = notTargetGroupsAudioAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        List<TextAds> locationFilteredTextAds = notTargetGroupsTextAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        List<SponsoredAds> locationFilteredSponsoredAds = notTargetGroupsSponsoredAds.stream()
                        .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                                .map(String::trim)
                                .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                        .collect(Collectors.toList());

        // Filter By Gender
        locationFilteredVideoAds = locationFilteredVideoAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        locationFilteredAudioAds = locationFilteredAudioAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        locationFilteredTextAds = locationFilteredTextAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        locationFilteredSponsoredAds = locationFilteredSponsoredAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        // Filter By Age
        locationFilteredVideoAds = locationFilteredVideoAds.stream()
                .filter(ad -> Arrays.stream(ad.getAge_group().split(","))
                        .map(String::trim)
                        .anyMatch(ageGroup -> ageGroup.equalsIgnoreCase(customer.getAgeGroupNo().toString())))
                .collect(Collectors.toList());

        locationFilteredAudioAds = locationFilteredAudioAds.stream()
                .filter(ad -> Arrays.stream(ad.getAge_group().split(","))
                        .map(String::trim)
                        .anyMatch(ageGroup -> ageGroup.equalsIgnoreCase(customer.getAgeGroupNo().toString())))
                .collect(Collectors.toList());

        locationFilteredTextAds = locationFilteredTextAds.stream()
                .filter(ad -> Arrays.stream(ad.getAge_group().split(","))
                        .map(String::trim)
                        .anyMatch(ageGroup -> ageGroup.equalsIgnoreCase(customer.getAgeGroupNo().toString())))
                .collect(Collectors.toList());

        locationFilteredSponsoredAds = locationFilteredSponsoredAds.stream()
                .filter(ad -> Arrays.stream(ad.getAge_group().split(","))
                        .map(String::trim)
                        .anyMatch(ageGroup -> ageGroup.equalsIgnoreCase(customer.getAgeGroupNo().toString())))
                .collect(Collectors.toList());

        // Filter By Device Type


//        List<AudioAds> unWatchedAudioAds = locationFilteredAudioAds.stream()
//                .filter(ad -> !ad.getIsWatched())
//                .collect(Collectors.toList());
//        List<AudioAds> watchedAudioAds = locationFilteredAudioAds.stream()
//                .filter(ad -> ad.getIsWatched())
//                .collect(Collectors.toList());
//
//        Collections.shuffle(unWatchedAudioAds);
//        Collections.shuffle(watchedAudioAds);
//        List<AudioAds> audioAdsList = Stream.concat(unWatchedAudioAds.stream(), watchedAudioAds.stream()).collect(Collectors.toList());
//
//        List<VideoAds> unWatchedVideoAds = locationFilteredVideoAds.stream()
//                .filter(ad -> !ad.getIsWatched())
//                .collect(Collectors.toList());
//        List<VideoAds> watchedVideoAds = locationFilteredVideoAds.stream()
//                .filter(ad -> ad.getIsWatched())
//                .collect(Collectors.toList());
//
//        Collections.shuffle(unWatchedVideoAds);
//        Collections.shuffle(watchedVideoAds);
//        List<VideoAds> videoAdsList = Stream.concat(unWatchedVideoAds.stream(), watchedVideoAds.stream()).collect(Collectors.toList());
//
//        List<TextAds> unWatchedTextAds = locationFilteredTextAds.stream()
//                .filter(ad -> !ad.getIsWatched())
//                .collect(Collectors.toList());
//        List<TextAds> watchedTextAds = locationFilteredTextAds.stream()
//                .filter(ad -> ad.getIsWatched())
//                .collect(Collectors.toList());
//
//        Collections.shuffle(unWatchedTextAds);
//        Collections.shuffle(watchedTextAds);
//        List<TextAds> textAdsList = Stream.concat(unWatchedTextAds.stream(), watchedTextAds.stream()).collect(Collectors.toList());
//
//        List<SponsoredAds> unWatchedSponsoredAds = sponsoredAds.stream()
//                .filter(ad -> !ad.getIsWatched())
//                .collect(Collectors.toList());
//        List<SponsoredAds> watchedSponsoredAds = sponsoredAds.stream()
//                .filter(ad -> ad.getIsWatched())
//                .collect(Collectors.toList());
//
//        Collections.shuffle(unWatchedSponsoredAds);
//        Collections.shuffle(watchedSponsoredAds);
//        List<SponsoredAds> sponsoredAdsList = Stream.concat(unWatchedSponsoredAds.stream(), watchedSponsoredAds.stream()).collect(Collectors.toList());



        List<AudioAds> finalAudioAds = Stream.concat(filteredAudioTargetGroupsAds.stream(), locationFilteredAudioAds.stream())
                .collect(Collectors.toList());
        List<TextAds> finalTextAds = Stream.concat(filteredTextTargetGroupsAds.stream(), locationFilteredTextAds.stream())
                .collect(Collectors.toList());
        List<VideoAds> finalVideoAds = Stream.concat(filteredVideoTargetGroupsAds.stream(), locationFilteredVideoAds.stream())
                .collect(Collectors.toList());
        List<SponsoredAds> finalSponsoredAds = Stream.concat(filteredSponsoredTargetGroupsAds.stream(), locationFilteredSponsoredAds.stream())
                .collect(Collectors.toList());


        log.info("targetGroupsVideoAds : {}", targetGroupsVideoAds.size());
        log.info("notTargetGroupsVideoAds : {}", notTargetGroupsVideoAds.size());
        log.info("filteredTargetGroupsAds : {}", filteredVideoTargetGroupsAds.size());
        log.info("finalAds : {}", finalVideoAds.size());


        Map<String, Object> response = new HashMap<>();

        response.put("videoAds", finalVideoAds);
        response.put("audioAds", finalAudioAds);
        response.put("textAds", finalTextAds);
        response.put("sponsoredAds", finalSponsoredAds);
        response.put("totalEarned", totalEarned);
        response.put("amountToBeEarned", moreToEarn);

        return ResponseHandler.generateResponse(HttpStatus.OK, response);
    }

    @GetMapping(path = "/video-ads")
    public ResponseEntity<Object> getRewardedVideoAds(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "industry", required = false) String industry,
            @RequestParam(value = "phoneNumber") String phoneNumber,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "age", required = false) String age
    ) {
        Map<String, Object> response = new HashMap<>();

        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + phoneNumber);

        String userAgeGroup = customer.getAgeGroupNo().toString();
        String userLocation = customer.getLocation().toLowerCase().trim();
        String userGender = customer.getGender().toLowerCase().trim();

        if (industry == null) {
            List<VideoAds> videoAds = rewardedVideoAdRepository.getActiveVideoAdsOnly(phoneNumber);
            List<VideoAds> targetGroupsVideoAds = videoAds.stream()
                    .filter(VideoAds::isTarget_group)
                    .collect(Collectors.toList());

            List<VideoAds> notTargetGroupsVideoAds = videoAds.stream()
                    .filter(ad -> !ad.isTarget_group())
                    .collect(Collectors.toList());
            List<VideoAds> filteredVideoTargetGroupsAds = targetGroupsVideoAds.stream()
                    .filter(ad -> {
                        String target_groups = ad.getTarget_groups();
                        List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        log.info("target_groups : {}", target_groups);
                        log.info("targetGroupNames : {}", targetGroupNames);
                        List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                        log.info("Target Groups : {}", targetGroups);
                        log.info(("Company : {}"), ad.getCompany());
                        log.info("targetGroups : {}", targetGroups.size());
                        return targetGroups.stream()
                                .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                    })
                    .collect(Collectors.toList());
            List<VideoAds> filteredVideoAds = CompetitiveSeparation.filterVideoAds(notTargetGroupsVideoAds, userLocation, userGender, userAgeGroup);
            List<VideoAds> finalAudioAds = Stream.concat(filteredVideoTargetGroupsAds.stream(), filteredVideoAds.stream())
                    .collect(Collectors.toList());
            // Companies company =  companiesRepository.findByName(videoAds.get(0).getCompany());
            // System.out.println("Video Ad Company : " + videoAds.get(0).getCompany());
            // System.out.println("Company : " + company);
//            List<VideoAds> filteredVideoAds = CompetitiveSeparation.filterVideoAds(videoAds, userLocation, userGender, userAgeGroup);
//            List<VideoAds> unWatchedVideoAds = filteredVideoAds.stream()
//                    .filter(ad -> !ad.getIsWatched())
//                    .collect(Collectors.toList());
//            List<VideoAds> watchedVideoAds = filteredVideoAds.stream()
//                    .filter(ad -> ad.getIsWatched())
//                    .collect(Collectors.toList());
//
//            Collections.shuffle(unWatchedVideoAds);
//            Collections.shuffle(watchedVideoAds);
//            List<VideoAds> videoAdsList = Stream.concat(unWatchedVideoAds.stream(), watchedVideoAds.stream()).collect(Collectors.toList());
            // response.put("videoAds", filteredVideoAds);
            response.put("videoAds", finalAudioAds);
            response.put("categories", industriesRepository.getIndustriesByFlagOrderByNameAsc("1"));
        } else {
            List<VideoAds> videoAds = rewardedVideoAdRepository.getRewardedAdsByIndustry(phoneNumber, industry);
//            List<VideoAds> filteredVideoAds = CompetitiveSeparation.filterVideoAds(videoAds, userLocation, userGender, userAgeGroup);
//            List<VideoAds> unWatchedVideoAds = filteredVideoAds.stream()
//                    .filter(ad -> !ad.getIsWatched())
//                    .collect(Collectors.toList());
//            List<VideoAds> watchedVideoAds = filteredVideoAds.stream()
//                    .filter(ad -> ad.getIsWatched())
//                    .collect(Collectors.toList());
//
//            Collections.shuffle(unWatchedVideoAds);
//            Collections.shuffle(watchedVideoAds);
//            List<VideoAds> videoAdsList = Stream.concat(unWatchedVideoAds.stream(), watchedVideoAds.stream()).collect(Collectors.toList());
            List<VideoAds> targetGroupsVideoAds = videoAds.stream()
                    .filter(VideoAds::isTarget_group)
                    .collect(Collectors.toList());

            List<VideoAds> notTargetGroupsVideoAds = videoAds.stream()
                    .filter(ad -> !ad.isTarget_group())
                    .collect(Collectors.toList());
            List<VideoAds> filteredVideoTargetGroupsAds = targetGroupsVideoAds.stream()
                    .filter(ad -> {
                        String target_groups = ad.getTarget_groups();
                        List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        log.info("target_groups : {}", target_groups);
                        log.info("targetGroupNames : {}", targetGroupNames);
                        List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                        log.info("Target Groups : {}", targetGroups);
                        log.info(("Company : {}"), ad.getCompany());
                        log.info("targetGroups : {}", targetGroups.size());
                        return targetGroups.stream()
                                .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                    })
                    .collect(Collectors.toList());
            List<VideoAds> filteredVideoAds = CompetitiveSeparation.filterVideoAds(notTargetGroupsVideoAds, userLocation, userGender, userAgeGroup);
            List<VideoAds> finalVideoAds = Stream.concat(filteredVideoTargetGroupsAds.stream(), filteredVideoAds.stream())
                    .collect(Collectors.toList());
            response.put("videoAds", finalVideoAds);
            response.put("categories", industriesRepository.getIndustriesByFlagOrderByNameAsc("1"));
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, response);
    }

    @GetMapping(path = "/audio-ads")
    public ResponseEntity<Object> getRewardedAudioAds(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "industry", required = false) String industry,
            @RequestParam(value = "phoneNumber") String phoneNumber,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "age", required = false) String age
    ) {
        Map<String, Object> response = new HashMap<>();

        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + phoneNumber);

        String userAgeGroup = customer.getAgeGroupNo().toString();
        String userLocation = customer.getLocation().toLowerCase().trim();
        String userGender = customer.getGender().toLowerCase().trim();

        if (industry == null) {
            List<AudioAds> audioAds = rewardedAudioAdsRepository.getActiveAudioAdsOnly(phoneNumber);
            List<AudioAds> targetGroupsAudioAds = audioAds.stream()
                    .filter(AudioAds::isTarget_group)
                    .collect(Collectors.toList());

            List<AudioAds> notTargetGroupsAudioAds = audioAds.stream()
                    .filter(ad -> !ad.isTarget_group())
                    .collect(Collectors.toList());
            List<AudioAds> filteredTextTargetGroupsAds = targetGroupsAudioAds.stream()
                    .filter(ad -> {
                        String target_groups = ad.getTarget_groups();
                        List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        log.info("target_groups : {}", target_groups);
                        log.info("targetGroupNames : {}", targetGroupNames);
                        List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                        log.info("Target Groups : {}", targetGroups);
                        log.info(("Company : {}"), ad.getCompany());
                        log.info("targetGroups : {}", targetGroups.size());
                        return targetGroups.stream()
                                .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                    })
                    .collect(Collectors.toList());
            List<AudioAds> filteredAudioAds = CompetitiveSeparation.filterAudioAds(notTargetGroupsAudioAds, userLocation, userGender, userAgeGroup);
            List<AudioAds> finalAudioAds = Stream.concat(filteredTextTargetGroupsAds.stream(), filteredAudioAds.stream())
                    .collect(Collectors.toList());
//            List<AudioAds> filteredAudioAds = CompetitiveSeparation.filterAudioAds(audioAds, userLocation, userGender, userAgeGroup);
//            List<AudioAds> unWatchedAudioAds = filteredAudioAds.stream()
//                    .filter(ad -> !ad.getIsWatched())
//                    .collect(Collectors.toList());
//            List<AudioAds> watchedAudioAds = filteredAudioAds.stream()
//                    .filter(ad -> ad.getIsWatched())
//                    .collect(Collectors.toList());
//
//            Collections.shuffle(unWatchedAudioAds);
//            Collections.shuffle(watchedAudioAds);
//            List<AudioAds> audioAdsList = Stream.concat(unWatchedAudioAds.stream(), watchedAudioAds.stream()).collect(Collectors.toList());
            response.put("audioAds", finalAudioAds);
            response.put("categories", industriesRepository.getIndustriesByFlagOrderByNameAsc("1"));
        } else {
            List<AudioAds> audioAds = rewardedAudioAdsRepository.getRewardedAudiAdsByIndustry(phoneNumber, industry);
            List<AudioAds> targetGroupsAudioAds = audioAds.stream()
                    .filter(AudioAds::isTarget_group)
                    .collect(Collectors.toList());

            List<AudioAds> notTargetGroupsAudioAds = audioAds.stream()
                    .filter(ad -> !ad.isTarget_group())
                    .collect(Collectors.toList());
            List<AudioAds> filteredTextTargetGroupsAds = targetGroupsAudioAds.stream()
                    .filter(ad -> {
                        String target_groups = ad.getTarget_groups();
                        List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        log.info("target_groups : {}", target_groups);
                        log.info("targetGroupNames : {}", targetGroupNames);
                        List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                        log.info("Target Groups : {}", targetGroups);
                        log.info(("Company : {}"), ad.getCompany());
                        log.info("targetGroups : {}", targetGroups.size());
                        return targetGroups.stream()
                                .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                    })
                    .collect(Collectors.toList());
            List<AudioAds> filteredAudioAds = CompetitiveSeparation.filterAudioAds(notTargetGroupsAudioAds, userLocation, userGender, userAgeGroup);
            List<AudioAds> finalAudioAds = Stream.concat(filteredTextTargetGroupsAds.stream(), filteredAudioAds.stream())
                    .collect(Collectors.toList());
//            List<AudioAds> filteredAudioAds = CompetitiveSeparation.filterAudioAds(audioAds, userLocation, userGender, userAgeGroup);
//            List<AudioAds> unWatchedAudioAds = filteredAudioAds.stream()
//                    .filter(ad -> !ad.getIsWatched())
//                    .collect(Collectors.toList());
//            List<AudioAds> watchedAudioAds = filteredAudioAds.stream()
//                    .filter(ad -> ad.getIsWatched())
//                    .collect(Collectors.toList());
//
//            Collections.shuffle(unWatchedAudioAds);
//            Collections.shuffle(watchedAudioAds);
//            List<AudioAds> audioAdsList = Stream.concat(unWatchedAudioAds.stream(), watchedAudioAds.stream()).collect(Collectors.toList());
            response.put("audioAds", finalAudioAds);
            response.put("categories", industriesRepository.getIndustriesByFlagOrderByNameAsc("1"));
        }
        return ResponseHandler.generateResponse(HttpStatus.OK,response);
    }

    @GetMapping(path = "/text-ads")
    public ResponseEntity<Object> getRewardedTextAds(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "industry", required = false) String industry,
            @RequestParam(value = "phoneNumber") String phoneNumber,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "age", required = false) String age
    ) {

        Map<String, Object> response = new HashMap<>();

        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber("+" + phoneNumber);
//        log.info("Customer 0000001: {}", customer);
        String userAgeGroup = customer.getAgeGroupNo().toString();
        String userLocation = customer.getLocation().toLowerCase().trim();
        String userGender = customer.getGender().toLowerCase().trim();

        if (industry == null) {
            List<TextAds> textAds = rewardedTextAdRepository.getRecentFiveAds(phoneNumber);
            List<TextAds> targetGroupsTextAds = textAds.stream()
                    .filter(TextAds::isTarget_group)
                    .collect(Collectors.toList());

            List<TextAds> notTargetGroupsTextAds = textAds.stream()
                    .filter(ad -> !ad.isTarget_group())
                    .collect(Collectors.toList());
            List<TextAds> filteredTextTargetGroupsAds = targetGroupsTextAds.stream()
                    .filter(ad -> {
                        String target_groups = ad.getTarget_groups();
                        List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        log.info("target_groups : {}", target_groups);
                        log.info("targetGroupNames : {}", targetGroupNames);
                        List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                        log.info("Target Groups : {}", targetGroups);
                        log.info(("Company : {}"), ad.getCompany());
                        log.info("targetGroups : {}", targetGroups.size());
                        return targetGroups.stream()
                                .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                    })
                    .collect(Collectors.toList());
            List<TextAds> filteredTextAds = CompetitiveSeparation.filterTextAds(notTargetGroupsTextAds, userLocation, userGender, userAgeGroup);
            List<TextAds> finalTextAds = Stream.concat(filteredTextTargetGroupsAds.stream(), filteredTextAds.stream())
                    .collect(Collectors.toList());
//            List<TextAds> unWatchedTextAds = filteredTextAds.stream()
//                    .filter(ad -> !ad.getIsWatched())
//                    .collect(Collectors.toList());
//            List<TextAds> watchedTextAds = filteredTextAds.stream()
//                    .filter(ad -> ad.getIsWatched())
//                    .collect(Collectors.toList());

//            Collections.shuffle(unWatchedTextAds);
//            Collections.shuffle(watchedTextAds);
//            List<TextAds> textAdsList = Stream.concat(unWatchedTextAds.stream(), watchedTextAds.stream()).collect(Collectors.toList());
            response.put("textAds", finalTextAds);
            response.put("categories", industriesRepository.getIndustriesByFlagOrderByNameAsc("1"));
        } else {
            List<TextAds> textAds = rewardedTextAdRepository.getRewardedTextAdsByIndustry(phoneNumber, industry);
            List<TextAds> targetGroupsTextAds = textAds.stream()
                    .filter(TextAds::isTarget_group)
                    .collect(Collectors.toList());

            List<TextAds> notTargetGroupsTextAds = textAds.stream()
                    .filter(ad -> !ad.isTarget_group())
                    .collect(Collectors.toList());
            List<TextAds> filteredTextTargetGroupsAds = targetGroupsTextAds.stream()
                    .filter(ad -> {
                        String target_groups = ad.getTarget_groups();
                        List<String> targetGroupNames = Arrays.stream(target_groups.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        log.info("target_groups : {}", target_groups);
                        log.info("targetGroupNames : {}", targetGroupNames);
                        List<TargetGroup> targetGroups = targetGroupsRepository.findTargetGroupsByCompanyNameAndGroupNameIn(ad.getCompany(), targetGroupNames);
                        log.info("Target Groups : {}", targetGroups);
                        log.info(("Company : {}"), ad.getCompany());
                        log.info("targetGroups : {}", targetGroups.size());
                        return targetGroups.stream()
                                .anyMatch(target -> target.getPhoneNumber().equals(phoneNumber));
                    })
                    .collect(Collectors.toList());
            List<TextAds> filteredTextAds = CompetitiveSeparation.filterTextAds(notTargetGroupsTextAds, userLocation, userGender, userAgeGroup);
            List<TextAds> finalTextAds = Stream.concat(filteredTextTargetGroupsAds.stream(), filteredTextAds.stream())
                    .collect(Collectors.toList());
//            List<TextAds> filteredTextAds = CompetitiveSeparation.filterTextAds(textAds, userLocation, userGender, userAgeGroup);
//            List<TextAds> unWatchedTextAds = filteredTextAds.stream()
//                    .filter(ad -> !ad.getIsWatched())
//                    .collect(Collectors.toList());
//            List<TextAds> watchedTextAds = filteredTextAds.stream()
//                    .filter(ad -> ad.getIsWatched())
//                    .collect(Collectors.toList());
//
//            Collections.shuffle(unWatchedTextAds);
//            Collections.shuffle(watchedTextAds);
//            List<TextAds> textAdsList = Stream.concat(unWatchedTextAds.stream(), watchedTextAds.stream()).collect(Collectors.toList());
            response.put("textAds", finalTextAds);
            response.put("categories", industriesRepository.getIndustriesByFlagOrderByNameAsc("1"));
        }
        return ResponseHandler.generateResponse(HttpStatus.OK,response);
    }

    @PostMapping(path = "/reward")
    public ResponseEntity<?> sendReward(
            @RequestHeader(value = "clientId") String clientId,
            @RequestBody RewardRequest rewardRequest
    ) {
        return ResponseEntity.ok(rewardService.rewardUserEndpoint(rewardRequest));
    }


    public void rewardUser(String adType, Long adID) throws MqttException {
        // Logic to create a user
        // subscribeSample.subscribeToMQTT();
        // System.out.println();
        // Publish user created event
        publishUser.publishMessage("RewardSuccess",adType + "," + adID);
        // eventPublisher.publishEvent();
    }

    @GetMapping(path = "/reward-report")
    public ResponseEntity<?> getUserRewardSummary(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phoneNumber") String phoneNumber) {
        return new ResponseEntity<>(rewardedAdsService.getAlluserAdRewards(phoneNumber), HttpStatus.OK);
    }

    /* Cash Rewards */
    @GetMapping("/audio-reward-report")
    public ResponseEntity<?> getUserAudioRewardSummary(
            @RequestHeader(value = "clientId") String clientId,
        @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(rewardedAdsService.getUserAudioAdRewards(phoneNumber), HttpStatus.OK);
    }

    @GetMapping("/text-reward-report")
    public ResponseEntity<?> getUserTextRewardSummary(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(rewardedAdsService.getUserTextAdRewards(phoneNumber), HttpStatus.OK);
    }

    @GetMapping("/video-reward-report")
    public ResponseEntity<?> getUserVideoRewardSummary(
            @RequestHeader(value = "clientId") String clientId,
        @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(rewardedAdsService.getUserVideoAdRewards(phoneNumber), HttpStatus.OK);
    }

    @GetMapping(path = "/sponsored-reward-report")
    public ResponseEntity<?> getSponsoredRewardSummary(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phoneNumber") String phoneNumber) {
        return new ResponseEntity<>(rewardedAdsService.getUserSponsoredRewards(phoneNumber), HttpStatus.OK);
    }

    @GetMapping(path = "/cash-sponsored-report")
    public ResponseEntity<?> getSponsoredCashRewardSummary(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phoneNumber") String phoneNumber) {
        return new ResponseEntity<>(sponsoredAnswersRepository.getAllByPhoneAndAdvertTypeOrderByIdDesc(phoneNumber, "cash"), HttpStatus.OK);
    }

    /* Voucher Rewards */
    @GetMapping("/voucher-video-rewards")
    public ResponseEntity<?> getVideoVoucherRewards(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(
                vouchersRepository.findAllByPhoneNumberAndAdTypeContainingIgnoreCaseOrderByIdDesc(phoneNumber, "video"),
                HttpStatus.OK
        );
    }
    @GetMapping("/voucher-audio-rewards")
    public ResponseEntity<?> getAudioVoucherRewards(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(
                vouchersRepository.findAllByPhoneNumberAndAdTypeContainingIgnoreCaseOrderByIdDesc(phoneNumber, "audio"),
                HttpStatus.OK
        );
    }
    @GetMapping("/voucher-text-rewards")
    public ResponseEntity<?> getTextVoucherRewards(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(
                vouchersRepository.findAllByPhoneNumberAndAdTypeContainingIgnoreCaseOrderByIdDesc(phoneNumber, "text"),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/voucher-sponsored-report")
    public ResponseEntity<?> getSponsoredVoucherRewardSummary(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phoneNumber") String phoneNumber) {
        return new ResponseEntity<>(sponsoredAnswersRepository.getAllByPhoneAndAdvertTypeOrderByIdDesc(phoneNumber, "coupon"), HttpStatus.OK);
    }


    /* Airtime Ads Rewards */
    @GetMapping("/airtime-text-rewards")
    public ResponseEntity<?> getTextAirtimeRewards(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(
                sendAirtimeService.getAllAirtimeTextRewards(phoneNumber, "text"),
                HttpStatus.OK
        );
    }
    @GetMapping("/airtime-audio-rewards")
    public ResponseEntity<?> getAudioAirtimeRewards(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(
                sendAirtimeService.getAllAirtimeAudioRewards(phoneNumber, "audio"),
                HttpStatus.OK
        );
    }
    @GetMapping("/airtime-video-rewards")
    public ResponseEntity<?> getVideoAirtimeRewards(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam("phoneNumber") String phoneNumber
    ) {
        return new ResponseEntity<>(
                sendAirtimeService.getAllAirtimeVideoRewards(phoneNumber, "video"),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/airtime-sponsored-report")
    public ResponseEntity<?> getSponsoredAirtimeRewardSummary(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phoneNumber") String phoneNumber) {
        return new ResponseEntity<>(sponsoredAnswersRepository.getAllByPhoneAndAdvertTypeOrderByIdDesc(phoneNumber, "airtime"), HttpStatus.OK);
    }


    @GetMapping("/user-vouchers")
    public ResponseEntity<?> getUserVouchers(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phone") String phoneNumber
    ) {
        return ResponseEntity.ok(rewardedAdsService.getUserVouchers(phoneNumber));
    }

    @GetMapping("/user-vouchers-all")
    public ResponseEntity<?> getAllUserVouchers(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "phone") String phoneNumber
    ) {
        return ResponseEntity.ok(rewardedAdsService.getUserVouchers(phoneNumber));
    }

    @GetMapping("/user-airtime")
    public ResponseEntity<?> getUserAirtimeRewards(
            @RequestHeader(value = "clientId") String clientId,
            @RequestParam(value = "mobile") String mobile){
        return ResponseEntity.ok(rewardedAdsService.getUserRewardedAirtime(mobile));
    }

    @PostMapping(path = "/rewarded-ads-transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> rewardedAdsAsyncResults(
            @RequestHeader(value = "clientId") String clientId,
            @RequestBody B2CTransactionAsyncResponse b2CTransactionAsyncResponse
    ) throws JsonProcessingException {
        log.info("============ Transaction Result =============");
        log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));

        RewardedAdsTransactionsAttempts mpesaTransactions = rewardedAdsTransactionsAttemptsRepository
                .findByConversationIDOrOriginatorConversationID(
                        b2CTransactionAsyncResponse.getResult().getConversationID(),
                        b2CTransactionAsyncResponse.getResult().getOriginatorConversationID()
                );

        log.info("========= Fetched Transaction Attempt =================");
        log.info("Attempt : {} ", mpesaTransactions);

        int responseCode = b2CTransactionAsyncResponse.getResult().getResultCode();

        if (responseCode == 0 && mpesaTransactions != null) {
            log.info("============ Successful Transaction Result =============");
            log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));

            RewardedAdsTransactions rewardedAdsTransactions = new RewardedAdsTransactions();
            rewardedAdsTransactions.setConversationId(b2CTransactionAsyncResponse.getResult().getConversationID());
            rewardedAdsTransactions.setOriginatorConversationId(b2CTransactionAsyncResponse.getResult().getOriginatorConversationID());
            rewardedAdsTransactions.setResultDesc(b2CTransactionAsyncResponse.getResult().getResultDesc());
            rewardedAdsTransactions.setResultType(b2CTransactionAsyncResponse.getResult().getResultType());
            rewardedAdsTransactions.setResultCode(String.valueOf(b2CTransactionAsyncResponse.getResult().getResultCode()));
            rewardedAdsTransactions.setAmount(b2CTransactionAsyncResponse.getResult().getResultParameters().getResultParameter().get(0).getValue());
            rewardedAdsTransactions.setTransactionReceipt(b2CTransactionAsyncResponse.getResult().getResultParameters().getResultParameter().get(1).getValue());
            rewardedAdsTransactions.setReceiverPublicName(b2CTransactionAsyncResponse.getResult().getResultParameters().getResultParameter().get(2).getValue());
            rewardedAdsTransactions.setTransactionCompletionDate(b2CTransactionAsyncResponse.getResult().getResultParameters().getResultParameter().get(3).getValue());
            rewardedAdsTransactions.setIsCustomerRegistered(b2CTransactionAsyncResponse.getResult().getResultParameters().getResultParameter().get(6).getValue());
            rewardedAdsTransactionRepository.save(rewardedAdsTransactions);
            if (mpesaTransactions.getAdvertType().equals("video")) {
                rewardedVideoAdRepository.closeAd(mpesaTransactions.getAdvertId());
                rewardedVideoRepository.validatePayment(0, mpesaTransactions.getPhoneNumber(), mpesaTransactions.getAdvertId());
            }

            if (mpesaTransactions.getAdvertType().equals("text")) {
                rewardedTextsRepository.validatePayment(0, mpesaTransactions.getPhoneNumber(), mpesaTransactions.getAdvertId());
                rewardedTextAdRepository.closeAd(mpesaTransactions.getAdvertId());
            }

            if (mpesaTransactions.getAdvertType().equals("audio")) {
                rewardedAudioRepository.validatePayment(0, mpesaTransactions.getPhoneNumber(), mpesaTransactions.getAdvertId());
                rewardedAudioAdsRepository.closeAd(mpesaTransactions.getAdvertId());
            }
        } else {
            log.info("============ Failed Transaction Result =============");
            log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));
        }
        return ResponseEntity.ok(acknowledgeResponse);
    }
}
