package com.teleeza.wallet.teleeza.topups.send_airtime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.daraja.config.MpesaConfiguration;
import com.teleeza.wallet.teleeza.daraja.repository.MpesaTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.repository.AirtimeRepository;
import com.teleeza.wallet.teleeza.topups.config.AirtimeConfig;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.requests.SendAirtimeRequest;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.AccessTokenResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.SendAirtimeResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.TupayAirtimeBalanceResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.TupayAirtimeResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.models.AirtimeAdRewards;
import com.teleeza.wallet.teleeza.topups.send_airtime.repository.EtopUpAirtimeRepository;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

import static com.teleeza.wallet.teleeza.utils.Constants.*;

@Service
@Slf4j
public class AirtimeService implements IAirtimeService {
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final AirtimeConfig airtimeConfig;
    private final EtopUpAirtimeRepository etopUpAirtimeRepository;

    @Autowired
    private MpesaTransactionsRepository mpesaTransactionsRepository;
    private final AirtimeRepository airtimeRepository;

    private final MpesaConfiguration mpesaConfiguration;

    // private final AirtimeConfiguration airtimeConfiguration;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    public final static String apiToken = "a5c3868dac290b23a0d4e24cefc6f297";
    public final static String apiUrl = "https://app.topups.co.ke/api/buy_airtime";

    public final static String balanceApiUrl = "https://app.topups.co.ke/api/get_balance";

    private static final String AUTH_URL = "https://api.tupay.africa:8888/v1/b2b/token";
    private static final String USERNAME = "2ff7225c-8ff1-549a-A52d-Daac65520a1a";
    private static final String PASSWORD = "EoshVQvP5_z6RztKz1Zq";
    private static String SEND_AIRTIME_URL = "https://api.tupay.africa:8888/v1/b2b/order/";
    private static String SAFARICOM_AIRTIME_URL = "https://api.tupay.africa:8888/v1/b2b/order/safaricom";
    private static String AIRTEL_AIRTIME_URL = "https://api.tupay.africa:8888/v1/b2b/order/airtel";
    private static String TELKOM_AIRTIME_URL = "https://api.tupay.africa:8888/v1/b2b/order/telkom";
    private static String EQUITEL_AIRTIME_URL = "https://api.tupay.africa:8888/v1/b2b/order/equitel";
    private static final String CHECK_BALANCE_URL = "https://api.tupay.africa:8888/v1/b2b/balance";

    public AirtimeService(RestTemplate restTemplate, HttpHeaders httpHeaders, AirtimeConfig airtimeConfig, EtopUpAirtimeRepository etopUpAirtimeRepository, AirtimeRepository airtimeRepository, MpesaConfiguration mpesaConfiguration, ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
        this.airtimeConfig = airtimeConfig;
        this.etopUpAirtimeRepository = etopUpAirtimeRepository;
        this.airtimeRepository = airtimeRepository;
        this.mpesaConfiguration = mpesaConfiguration;
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public AccessTokenResponse getAccessToken() {

        // get the Base64 rep of consumerKey + ":" + consumerSecret
        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s", mpesaConfiguration.getConsumerKey(),
                mpesaConfiguration.getConsumerSecret()));

        Request request = new Request.Builder()
                .url(String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType()))
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            // use Jackson to Decode the ResponseBody ...
            return objectMapper.readValue(response.body().string(), AccessTokenResponse.class);
        } catch (IOException e) {
            // log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    public void checkPhoneNumber(String phoneNumber){
        log.info("Checking Phone Number {}", phoneNumber);
        List<String> safaricomPhoneNumbers = new ArrayList<String>(Arrays.asList(new String[]{
                "110", "0111", "25470", "25471",
                "25472", "254740", "254741", "254742", "254743",
                "254745", "254746", "254748", "254757", "254758",
                "254759", "254768", "254769", "25479"})
        );
        List<String> airtelPhoneNumbers = new ArrayList<String>(Arrays.asList(new String[]{
                "25410", "2543", "254751", "254752", "254753",
                "254754", "254755", "254756",
                "254762", "25478"}));

        List<String> telkomPhoneNumbers = new ArrayList<String>(Arrays.asList(new String[]{
                "25477"}));

        List<String> equitelPhoneNumbers = new ArrayList<String>(Arrays.asList(new String[]{
                "254763",
                "254764",
                "254765",
                "254766",
        }));

        if(phoneNumber.startsWith("0")){
            phoneNumber.replaceFirst("0", "254");
        }
        if(phoneNumber.startsWith("+")){
            phoneNumber.replaceFirst("/+", "");
        }
        for (String prefix : safaricomPhoneNumbers) {
            if (phoneNumber.startsWith(prefix)) {
                SEND_AIRTIME_URL = SAFARICOM_AIRTIME_URL;
                break;
            }
        }
        for (String prefix : airtelPhoneNumbers) {
            if (phoneNumber.startsWith(prefix)) {
                SEND_AIRTIME_URL = AIRTEL_AIRTIME_URL;
                break;
            }
        }
        for (String prefix : telkomPhoneNumbers) {
            if (phoneNumber.startsWith(prefix)) {
                SEND_AIRTIME_URL = TELKOM_AIRTIME_URL;
                break;
            }
        }
        for (String prefix : equitelPhoneNumbers) {
            if (phoneNumber.startsWith(prefix)) {
                SEND_AIRTIME_URL = EQUITEL_AIRTIME_URL;
                break;
            }
        }
        log.info("Send Airtime URL {}", SEND_AIRTIME_URL);
    }
    @Override
    public AccessTokenResponse getTupayAccessToken() {
        // Encode Account UUID and API Key for Basic Authentication
        String credentials = USERNAME + ":" + PASSWORD;
        String base64Credentials = Base64Utils.encodeToString(credentials.getBytes());

        // Prepare headers with Authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Prepare request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        // Make a POST request to the authentication endpoint
        ResponseEntity<AccessTokenResponse> responseEntity = new RestTemplate().exchange(
                AUTH_URL,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                AccessTokenResponse.class
        );

        // Handle the response
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Authentication failed. Status code: " + responseEntity.getStatusCode());
        }
    }

    @Override
    public TupayAirtimeBalanceResponse getBalance() {

        AccessTokenResponse accessTokenResponse = getTupayAccessToken();
        HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessTokenResponse.getAccess_token());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

// Use exchange method to include headers in the request
        ResponseEntity<TupayAirtimeBalanceResponse> responseEntity = restTemplate.exchange(
                CHECK_BALANCE_URL,
                HttpMethod.GET,
                requestEntity,
                TupayAirtimeBalanceResponse.class
        );

// Extract the response body from the ResponseEntity
        TupayAirtimeBalanceResponse tupayAirtimeBalanceResponse = responseEntity.getBody();


        return tupayAirtimeBalanceResponse;

    }

    @Override
    public SendAirtimeResponse sendAirtimeToUser(SendAirtimeRequest sendAirtimeReq) {
        // Obtain the access token using getToken method
        AccessTokenResponse authResponse = getTupayAccessToken();


        // log.info("Access Token {}", authResponse.getAccess_token());
        // Prepare headers with Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authResponse.getAccess_token());
        String uid = UUID.randomUUID().toString();
        headers.set("Idempotency-Key", uid);

        // Prepare request body
        HttpEntity<SendAirtimeRequest> requestEntity = new HttpEntity<>(sendAirtimeReq, headers);

        // Make a POST request to the send airtime endpoint
        ResponseEntity<SendAirtimeResponse> responseEntity = new RestTemplate().exchange(
                SEND_AIRTIME_URL, // Replace with your send airtime endpoint
                HttpMethod.POST,
                requestEntity,
                SendAirtimeResponse.class
        );

        // Handle the response
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Send airtime request failed. Status code: " + responseEntity.getStatusCode());
        }
    }


    @Override
    public TupayAirtimeResponse sendAirtimeToPerson(String account, String amount) {
        // Obtain the access token using getToken method
        AccessTokenResponse authResponse = getTupayAccessToken();

        // Prepare headers with Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authResponse.getAccess_token());

        Map<String, Object> sendAirtimeReq = new HashMap<>();
        sendAirtimeReq.put("currency", "KES");
        sendAirtimeReq.put("account", account);
        sendAirtimeReq.put("amount", amount);

        checkPhoneNumber(account);

        HttpEntity<Map<String, Object>> sendAirtimeRequest = new HttpEntity<>(sendAirtimeReq, headers);
//        ResponseEntity<TupayAirtimeResponse> sendAirtimeResponse = restTemplate.postForEntity(
//                airtimeConfig.getAirtimeUrl(),
//                sendAirtimeRequest, TupayAirtimeResponse.class);

        ResponseEntity<TupayAirtimeResponse> sendAirtimeResponse = restTemplate.postForEntity(
                SEND_AIRTIME_URL,
                sendAirtimeRequest, TupayAirtimeResponse.class);


        log.info("sendAirtimeResponse {}", sendAirtimeResponse);

        if (sendAirtimeResponse.getStatusCode() == HttpStatus.OK) {
            log.info("Success");
            return sendAirtimeResponse.getBody();
        }
        return sendAirtimeResponse.getBody();
    }

    @Override
    public SendAirtimeResponse sendAirtime(SendAirtimeRequest sendAirtimeRequest) {

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        // httpHeaders.setBearerAuth(topupsConfig.getAirtimeApiKey());
        httpHeaders.setCacheControl("private, no-store, max-age=0");
        httpHeaders.setExpires(0);

        // log.info("Request : {}", sendAirtimeRequest);

        Map<String, Object> request = new HashMap<>();
        request.put("account", sendAirtimeRequest.getAccount());
        request.put("amount", sendAirtimeRequest.getAmount());
        request.put("currency",sendAirtimeRequest.getCurrency());
        request.put("reference",sendAirtimeRequest.getReference());

        HttpEntity<Map<String, Object>> disburseAirtimeRequest = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<SendAirtimeResponse> sendAirtimeResponse = restTemplate.postForEntity(
                airtimeConfig.getOrderUrl(),
                disburseAirtimeRequest, SendAirtimeResponse.class);

        if(sendAirtimeResponse.getStatusCode() == HttpStatus.OK){

            // log.info("Sending Airtime : {} ", sendAirtimeResponse.getBody());

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
            // log.info("Sending Airtime Failed: {} ", sendAirtimeResponse.getBody());
            return sendAirtimeResponse.getBody();
        }

    }
}
