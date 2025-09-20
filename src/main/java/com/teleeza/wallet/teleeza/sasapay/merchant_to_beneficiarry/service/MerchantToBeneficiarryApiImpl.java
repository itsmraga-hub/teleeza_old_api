package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.MerchantToBeneficiarryAuthResponse;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MerchantToBeneficiarryApiImpl implements MerchantToBeneficiarryApi{
    private final SasaPayConfig sasaPayConfig;
    @Autowired private  OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    @Autowired private RestTemplate restTemplate;
    @Autowired private HttpHeaders httpHeaders;

    public MerchantToBeneficiarryApiImpl(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public MerchantToBeneficiarryAuthResponse getAccessToken() {
//        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s",
//                sasaPayConfig.getMerchantToBeneficiarryCliendID(), sasaPayConfig.getMerchantToBeneficiarrySecret()));
//
//        log.info("=====BASIC TOKEN=====");
//        log.info(String.format("BEARER %s", encodedCredentials));
//
//        Request request = new Request.Builder()
//                .url(String.format("%s?grant_type=%s", sasaPayConfig.getOauthEndPoint(), sasaPayConfig.getGrantType()))
//                .get()
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
//                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
//                .build();
//
//
//        log.info(" Auth request{}", request);
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            assert response.body() != null;
//            // Deserialize response body to Java object
//            return objectMapper.readValue(response.body().string(), MerchantToBeneficiarryAuthResponse.class);
//        } catch (IOException e) {
//            log.error("Could not get access token. {}", e.getLocalizedMessage());
//            return null;
//        }
//    }

        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s",
                sasaPayConfig.getClientID(), sasaPayConfig.getClientSecret()));

        String url = String.format("%s?grant_type=%s", sasaPayConfig.getOauthEndPoint(), sasaPayConfig.getGrantType());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(sasaPayConfig.getMerchantToBeneficiarryCliendID(), sasaPayConfig.getMerchantToBeneficiarrySecret());
//        httpHeaders.setBasicAuth(AUTHORIZATION_HEADER_STRING,String.format("%s %s",BASIC_AUTH_STRING,encodedCredentials));

        // build the request
        HttpEntity request = new HttpEntity(httpHeaders);

        // make an HTTP GET request with headers
        ResponseEntity<MerchantToBeneficiarryAuthResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                MerchantToBeneficiarryAuthResponse.class

        );

        // check response
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return response.getBody();
        }
    }
}
