package com.teleeza.wallet.teleeza.topups.send_airtime.service.impl;

import com.teleeza.wallet.teleeza.topups.config.TopupsConfig;
import com.teleeza.wallet.teleeza.topups.config.TupayConfig;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.requests.SendAirtimeRequest;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.SendAirtimeResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.models.AirtimeAdRewards;
import com.teleeza.wallet.teleeza.topups.send_airtime.repository.EtopUpAirtimeRepository;
import com.teleeza.wallet.teleeza.topups.send_airtime.service.SendAirtimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class SendAirtimeServiceImpl implements SendAirtimeService {
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final TopupsConfig topupsConfig;
    private final TupayConfig tupayConfig;
    private final EtopUpAirtimeRepository etopUpAirtimeRepository;

    public SendAirtimeServiceImpl(RestTemplate restTemplate, HttpHeaders httpHeaders, TopupsConfig topupsConfig, TupayConfig tupayConfig, EtopUpAirtimeRepository etopUpAirtimeRepository) {
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
        this.topupsConfig = topupsConfig;
        this.tupayConfig = tupayConfig;
        this.etopUpAirtimeRepository = etopUpAirtimeRepository;
    }

    @Override
    public SendAirtimeResponse sendAirtime(SendAirtimeRequest sendAirtimeRequest) {

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(topupsConfig.getAirtimeApiKey());
        httpHeaders.setCacheControl("private, no-store, max-age=0");
        httpHeaders.setExpires(0);

        log.info("Request : {}", sendAirtimeRequest);

        Map<String, Object> request = new HashMap<>();
        request.put("country","KE");
        request.put("network","Safaricom");
        request.put("name", sendAirtimeRequest.getName());
        request.put("mobile", sendAirtimeRequest.getMobile());
        request.put("email",sendAirtimeRequest.getEmail());
        request.put("currency","KES");
        request.put("amount",sendAirtimeRequest.getAmount());
        request.put("note","");

        HttpEntity<Map<String, Object>> disburseAirtimeRequest = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<SendAirtimeResponse> sendAirtimeResponse = restTemplate.postForEntity(
                topupsConfig.getTopupBaseUrl(),
                disburseAirtimeRequest, SendAirtimeResponse.class);

        if(sendAirtimeResponse.getStatusCode() == HttpStatus.OK){
            log.info("Sending Airtime : {} ", sendAirtimeResponse.getBody());

            AirtimeAdRewards airtimeAdRewards = new AirtimeAdRewards();
            airtimeAdRewards.setCountry("KE");
            airtimeAdRewards.setCurrency("KES");
            airtimeAdRewards.setTitle(sendAirtimeRequest.getAdTitle());
            airtimeAdRewards.setMobile(sendAirtimeResponse.getBody().getData().getRecipient().getMobile());
            airtimeAdRewards.setAmount(sendAirtimeRequest.getAmount());
            airtimeAdRewards.setCode(Objects.requireNonNull(sendAirtimeResponse.getBody()).getStatus().getCode());
            airtimeAdRewards.setStatus(sendAirtimeResponse.getBody().getStatus().getType());
            airtimeAdRewards.setReference(sendAirtimeResponse.getBody().getData().getTransaction().getReference());
            airtimeAdRewards.setAdType(sendAirtimeRequest.getAdType());
            etopUpAirtimeRepository.save(airtimeAdRewards);

            return sendAirtimeResponse.getBody();
        }else {
            AirtimeAdRewards airtimeAdRewards = new AirtimeAdRewards();
            airtimeAdRewards.setCountry("KE");
            airtimeAdRewards.setCurrency("KES");
            airtimeAdRewards.setTitle(sendAirtimeRequest.getAdTitle());
            airtimeAdRewards.setMobile(sendAirtimeResponse.getBody().getData().getRecipient().getMobile());
            airtimeAdRewards.setAmount(sendAirtimeRequest.getAmount());
            airtimeAdRewards.setCode(Objects.requireNonNull(sendAirtimeResponse.getBody()).getStatus().getCode());
            airtimeAdRewards.setStatus(sendAirtimeResponse.getBody().getStatus().getType());
            airtimeAdRewards.setReference(sendAirtimeResponse.getBody().getData().getTransaction().getReference());
            etopUpAirtimeRepository.save(airtimeAdRewards);
            log.info("Sending Airtime Failed: {} ", sendAirtimeResponse.getBody());
            return sendAirtimeResponse.getBody();
        }

    }

    @Override
    public List<AirtimeAdRewards> getAllAirtimeTextRewards(String mobile, String adType) {
        List<AirtimeAdRewards> textRewards;
        textRewards = etopUpAirtimeRepository.getAirtimeAdRewardsByMobileAndAdTypeContainingIgnoreCaseOrderByIdDesc(mobile, adType);
        return textRewards;
    }

    @Override
    public List<AirtimeAdRewards> getAllAirtimeVideoRewards(String mobile, String adType) {
        List<AirtimeAdRewards> videoRewards;
        videoRewards = etopUpAirtimeRepository.getAirtimeAdRewardsByMobileAndAdTypeContainingIgnoreCaseOrderByIdDesc(mobile, adType);
        return videoRewards;
    }

    @Override
    public List<AirtimeAdRewards> getAllAirtimeAudioRewards(String mobile, String adType) {
        List<AirtimeAdRewards> audioRewards;
        audioRewards = etopUpAirtimeRepository.getAirtimeAdRewardsByMobileAndAdTypeContainingIgnoreCaseOrderByIdDesc(mobile, adType);
        return audioRewards;
    }

//    @Override
//    public List<AirtimeAdRewards> getAllAirtimeTextRewardsOrderByIdDesc(String phoneNumber, String text) {
//        return null;
//    }
//
//    @Override
//    public List<AirtimeAdRewards> getAllAirtimeAudioRewardsOrderByIdDesc(String phoneNumber, String audio) {
//        return null;
//    }
//
//    @Override
//    public List<AirtimeAdRewards> getAllAirtimeVideoRewardsOrderByIdDesc(String phoneNumber, String video) {
//        return null;
//    }
}
