package com.teleeza.wallet.teleeza.subscription.service;

import com.teleeza.wallet.teleeza.common.config.MelioraConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MelioraSubscriptionService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private MelioraConfig melioraConfig;

    public void sendSubscriptionStatus(String subscriberId,String cycle,String cyclesNumber){

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> subscription = new HashMap<>();
        subscription.put("productId:", melioraConfig.getProductId());
        subscription.put("subscriberId", subscriberId);
        subscription.put("cycle", cycle);
        subscription.put("cyclesNumber", cyclesNumber);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(subscription, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(
                melioraConfig.getBaseUrlEndpoint(),
                entity, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful");
            System.out.println(response.getBody());
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }
    }
}
