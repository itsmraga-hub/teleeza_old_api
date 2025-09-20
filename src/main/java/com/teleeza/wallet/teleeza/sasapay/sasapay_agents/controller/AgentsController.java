package com.teleeza.wallet.teleeza.sasapay.sasapay_agents.controller;

import com.teleeza.wallet.teleeza.sasapay.sasapay_agents.dtos.responses.AgentsResponse;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("v1/teleeza-wallet/")
@Slf4j
public class AgentsController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private SasaPayApi sasaPayApi;


    @GetMapping("/agents")
    public AgentsResponse getSasaPayAgents(@RequestParam String Longitude, @RequestParam String Latitude) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(sasaPayApi.getAccessToken().getAccessToken());

        String url = String.format("%s?Longitude=%s&Latitude=%s", "https://api.sasapay.app/api/v1/waas/nearest-agent/", Longitude, Latitude);

        // build the request
        HttpEntity request = new HttpEntity(httpHeaders);

        // make an HTTP GET request with headers
        ResponseEntity<AgentsResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                AgentsResponse.class

        );

        // check response
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful.");
            System.out.println(response.getBody());
            Map<String, Object> map = new HashMap<String, Object>();
            return response.getBody();
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
            return response.getBody();
        }
    }
}
