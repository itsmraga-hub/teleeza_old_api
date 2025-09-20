package com.teleeza.wallet.teleeza.advanta.service;

import com.teleeza.wallet.teleeza.advanta.dtos.response.AdvantaMessageResponse;
import com.teleeza.wallet.teleeza.advanta.config.AdvantaApiConfig;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AdvantaSmsApiImpl  {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private AdvantaApiConfig advantaApiConfig;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;


    public AdvantaMessageResponse sendSmsNotification(String message, String mobile) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> notification = new HashMap<>();
        notification.put("apikey", advantaApiConfig.getApiKey());
        notification.put("partnerID", advantaApiConfig.getPartnerID());
        notification.put("message", message);
        notification.put("shortcode", advantaApiConfig.getShortCode());
        notification.put("mobile", mobile);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(notification, httpHeaders);
        ResponseEntity<AdvantaMessageResponse> response = restTemplate.postForEntity(
                advantaApiConfig.getBaseUrl(),
                entity, AdvantaMessageResponse.class);

        // check response
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println(response.getStatusCode());
            return response.getBody();
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
            return response.getBody();
        }
    }


    public AdvantaMessageResponse sendOtp(String message, String mobile) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> notification = new HashMap<>();
        notification.put("apikey", advantaApiConfig.getApiKey());
        notification.put("partnerID", advantaApiConfig.getPartnerID());
        notification.put("message", message);
        notification.put("shortcode", advantaApiConfig.getShortCode());
        notification.put("mobile", mobile);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(notification, httpHeaders);
        ResponseEntity<AdvantaMessageResponse> response = restTemplate.postForEntity(
                advantaApiConfig.getBaseUrl(),
                entity, AdvantaMessageResponse.class);

        // check response
        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful");
            String messageid = Objects.requireNonNull(response.getBody()).getResponses().get(0).getMessageid();
            String phone = "+" + Objects.requireNonNull(response.getBody()).getResponses().get(0).getMobile();
            System.out.println(response.getStatusCode());
            customerRegistrationRepository.updateUserRecordWithMessageId(messageid,phone);
            return response.getBody();
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
            return response.getBody();
        }
    }
}
