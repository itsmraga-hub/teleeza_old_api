package com.teleeza.wallet.teleeza.daraja.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.bima.config.BimaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.daraja.account_balance.dtos.requests.CheckAccountBalanceRequest;
import com.teleeza.wallet.teleeza.daraja.account_balance.dtos.response.CheckAccountBalanceResponse;
import com.teleeza.wallet.teleeza.daraja.auth.AccessTokenResponse;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.requests.B2CTransactionRequest;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.requests.InternalB2CTransactionRequest;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses.B2CTransactionResponse;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.requests.RegisterUrlRequest;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.requests.SimulateTransactionRequest;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.responses.RegisterUrlResponse;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.responses.SimulateTransactionResponse;
import com.teleeza.wallet.teleeza.daraja.config.MpesaConfiguration;
import com.teleeza.wallet.teleeza.daraja.entity.MpesaTransactions;
import com.teleeza.wallet.teleeza.daraja.repository.MpesaTransactionsRepository;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushResponse;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.requests.InternalStkPushRequest;
import com.teleeza.wallet.teleeza.notification.service.PushNotificationService;
import com.teleeza.wallet.teleeza.rewarded_ads.dtos.Request.RewardRequest;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedAdsTransactionsAttempts;
import com.teleeza.wallet.teleeza.rewarded_ads.repository.RewardedAdsTransactionsAttemptsRepository;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionRepository;
import com.teleeza.wallet.teleeza.subscription.service.FreemiumSubscriptionService;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.teleeza.wallet.teleeza.utils.Constants.*;

@Service
@Slf4j
public class DarajaApiImpl implements DarajaApi {
    private final MpesaConfiguration mpesaConfiguration;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private MpesaTransactionsRepository mpesaTransactionsRepository;
    @Autowired
    private RewardedAdsTransactionsAttemptsRepository rewardedAdsTransactionsAttemptsRepository;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;
//    @Autowired
//    private AdvantaSmsApiImpl advantaSmsApiImpl;
//    @Autowired
//    private PushNotificationService pushNotificationService;
//    @Autowired
//    private SubscriptionRepository subscriptionRepository;
//    @Autowired
//    private FreemiumSubscriptionService freemiumSubscriptionService;

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;


    public DarajaApiImpl(MpesaConfiguration mpesaConfiguration,
                         OkHttpClient okHttpClient,
                         ObjectMapper objectMapper, BimaConfig bimaConfig) {
        this.mpesaConfiguration = mpesaConfiguration;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AccessTokenResponse getAccessToken() {

        // get the Base64 rep of consumerKey + ":" + consumerSecret
        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s", mpesaConfiguration.getConsumerKey(),
                mpesaConfiguration.getConsumerSecret()));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials));
        headers.set(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL_HEADER_VALUE);

        String url = String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType());

        try {
            ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), AccessTokenResponse.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error(String.format("Could not get access token. -> %s", e.getMessage()));
            return null;
        }
//
//        Request request = new Request.Builder()
//                .url(String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType()))
//                .get()
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
//                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
//                .build();
//
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            assert response.body() != null;
//
//            // use Jackson to Decode the ResponseBody ...
//            return objectMapper.readValue(response.body().string(), AccessTokenResponse.class);
//        } catch (IOException e) {
//            log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
//            return null;
//        }

//        String url = String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType());
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        httpHeaders.setBasicAuth(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials));
//
//        // build the request
//        HttpEntity request = new HttpEntity(httpHeaders);
//
//        // make an HTTP GET request with headers
//        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                request,
//                AccessTokenResponse.class
//
//        );
//
//        // check response
//        if (response.getStatusCode() == HttpStatus.OK) {
//            return response.getBody();
//        } else {
//            return response.getBody();
//        }
    }

    @Override
    public AccessTokenResponse getB2CAccessToken() {

        // get the Base64 rep of consumerKey + ":" + consumerSecret
        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s", mpesaConfiguration.getB2cConsumerKey(),
                mpesaConfiguration.getB2cConsumerSecret()));

//        //        // get the Base64 rep of consumerKey + ":" + consumerSecret
//        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s", mpesaConfiguration.getConsumerKey(),
//                mpesaConfiguration.getConsumerSecret()));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials));
        headers.set(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL_HEADER_VALUE);

        String url = String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType());

        try {
            ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), AccessTokenResponse.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error(String.format("Could not get access token. -> %s", e.getMessage()));
            return null;
        }

    }

    @Override
    public RegisterUrlResponse registerUrl() {
        AccessTokenResponse accessTokenResponse = getAccessToken();

        RegisterUrlRequest registerUrlRequest = new RegisterUrlRequest();
        registerUrlRequest.setConfirmationURL(mpesaConfiguration.getConfirmationURL());
        registerUrlRequest.setResponseType(mpesaConfiguration.getResponseType());
        registerUrlRequest.setShortCode(mpesaConfiguration.getShortCode());
        registerUrlRequest.setValidationURL(mpesaConfiguration.getValidationURL());


        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(registerUrlRequest)));

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getRegisterUlrEndPoint())
                .post(body)
                .addHeader("Authorization", String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            assert response.body() != null;
            // use Jackson to Decode the ResponseBody ...
            return objectMapper.readValue(response.body().string(), RegisterUrlResponse.class);

        } catch (IOException e) {
            log.error(String.format("Could not register url -> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    @Override
    @Transactional
    public SimulateTransactionResponse simulateC2BTransaction(SimulateTransactionRequest simulateTransactionRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();
        log.info(String.format("Access Token: %s", accessTokenResponse.getAccessToken()));

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());

        Map<String, Object> simulateC2BRequest = new HashMap<>();
        simulateC2BRequest.put("ShortCode", mpesaConfiguration.getShortCode());
        simulateC2BRequest.put("Amount", simulateTransactionRequest.getAmount());
        simulateC2BRequest.put("CommandID", CUSTOMER_PAYBILL_ONLINE);
        simulateC2BRequest.put("Msisdn", simulateTransactionRequest.getMsisdn());
        simulateC2BRequest.put("BillRefNumber", simulateTransactionRequest.getBillRefNumber());

        HttpEntity<Map<String, Object>> simulateC2B = new HttpEntity<>(simulateC2BRequest, httpHeaders);
        ResponseEntity<SimulateTransactionResponse> simulateTransactionResponse = restTemplate.postForEntity(
                mpesaConfiguration.getSimulateC2BTransactionEndpoint(),
                simulateC2B, SimulateTransactionResponse.class);

        MpesaTransactions mpesaTransactions = new MpesaTransactions();
        if (simulateTransactionResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful");

            CustomerEntity customerEntity = customerRegistrationRepository.findByCustomerAccountNumber(
                    simulateTransactionRequest.getBillRefNumber()
            );

            mpesaTransactions.setTransactionType(CUSTOMER_PAYBILL_ONLINE);
            mpesaTransactions.setAmount(simulateTransactionRequest.getAmount());
            mpesaTransactions.setPhoneNumber(simulateTransactionRequest.getMsisdn());
            mpesaTransactions.setPartyB(mpesaConfiguration.getStkPushShortCode());
            mpesaTransactions.setAccountReference(simulateTransactionRequest.getBillRefNumber());
            mpesaTransactions.setTransactionDesc("Freemium Subscription");
            mpesaTransactions.setTimeStamp(HelperUtility.getTransactionTimestamp());
            mpesaTransactions.setConversationID(simulateTransactionResponse.getBody().getConversationID());
            mpesaTransactions.setOriginatorConversationID(simulateTransactionResponse.getBody().getOriginatorCoversationID());
            mpesaTransactions.setResponseCode(simulateTransactionResponse.getBody().getResponseCode());
            mpesaTransactions.setResponseDescription(simulateTransactionResponse.getBody().getResponseDescription());
            mpesaTransactionsRepository.save(mpesaTransactions);

            return simulateTransactionResponse.getBody();
        } else {
            System.out.println("Request Failed");
            return simulateTransactionResponse.getBody();
        }
    }

    @Override
    @Transactional
    public B2CTransactionResponse performB2CTransaction(InternalB2CTransactionRequest internalB2CTransactionRequest) {
        AccessTokenResponse accessTokenResponse = getB2CAccessToken();

        B2CTransactionRequest b2CTransactionRequest = new B2CTransactionRequest();

        b2CTransactionRequest.setCommandID(internalB2CTransactionRequest.getCommandID());
        b2CTransactionRequest.setAmount(internalB2CTransactionRequest.getAmount());
        b2CTransactionRequest.setPartyB(internalB2CTransactionRequest.getPartyB());
        b2CTransactionRequest.setRemarks(internalB2CTransactionRequest.getRemarks());
        b2CTransactionRequest.setOccassion(internalB2CTransactionRequest.getOccassion());

        // get the security credentials ...
        b2CTransactionRequest.setSecurityCredential(mpesaConfiguration.getInitiatorPassword());

        // set the result url ...
        b2CTransactionRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());
        b2CTransactionRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        b2CTransactionRequest.setInitiatorName(mpesaConfiguration.getInitiatorName());
        b2CTransactionRequest.setPartyA("3029499");

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(b2CTransactionRequest)));

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getB2cTransactionEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            assert response.body() != null;

            return objectMapper.readValue(response.body().string(), B2CTransactionResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not perform B2C transaction ->%s", e.getLocalizedMessage()));
            return null;
        }
    }

    @Override
    public StkPushResponse performStkPushTransaction(InternalStkPushRequest internalStkPushRequest) {

        String transactionTimestamp = HelperUtility.getTransactionTimestamp();
        String stkPushPassword = HelperUtility.getStkPushPassword(mpesaConfiguration.getStkPushShortCode(),
                mpesaConfiguration.getStkPassKey(), transactionTimestamp);

        AccessTokenResponse accessTokenResponse = getAccessToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());

        Map<String, Object> stkPushRequest = new HashMap<>();
        stkPushRequest.put("TransactionType", CUSTOMER_PAYBILL_ONLINE);
        stkPushRequest.put("Amount", internalStkPushRequest.getAmount());
        stkPushRequest.put("BusinessShortCode", mpesaConfiguration.getStkPushShortCode());
        stkPushRequest.put("CallBackURL", mpesaConfiguration.getJiinueCallBack());
        stkPushRequest.put("PhoneNumber", internalStkPushRequest.getPhoneNumber());
        stkPushRequest.put("PartyA", internalStkPushRequest.getPhoneNumber());
        stkPushRequest.put("PartyB", mpesaConfiguration.getStkPushShortCode());
        stkPushRequest.put("AccountReference", internalStkPushRequest.getAccountReference());
        stkPushRequest.put("TransactionDesc", "Subscription");
        stkPushRequest.put("Timestamp", transactionTimestamp);
        stkPushRequest.put("Password", stkPushPassword);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(stkPushRequest, httpHeaders);
        ResponseEntity<StkPushResponse> stkPushResponse = restTemplate.postForEntity(
                mpesaConfiguration.getStkPushRequestUrl(),
                request, StkPushResponse.class);


        MpesaTransactions mpesaTransactions = new MpesaTransactions();
        if (stkPushResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful");

            CustomerEntity customerEntity = customerRegistrationRepository.findCustomerByPhoneNumber(
                    internalStkPushRequest.getAccountReference()
            );
            Boolean existsByReferralCode = customerRegistrationRepository.existsByReferralCode(
                    internalStkPushRequest.getReferredByCode()
            );
            if (existsByReferralCode.equals(true)) {
                log.info("Exist By Referral Code : {}", existsByReferralCode);
                customerEntity.setReferredByCode(internalStkPushRequest.getReferredByCode().toUpperCase());
                customerRegistrationRepository.save(customerEntity);
            }

            mpesaTransactions.setTransactionType(CUSTOMER_PAYBILL_ONLINE);
            mpesaTransactions.setAmount(internalStkPushRequest.getAmount());
            mpesaTransactions.setPhoneNumber(internalStkPushRequest.getPhoneNumber());
            mpesaTransactions.setPartyA(internalStkPushRequest.getPhoneNumber());
            mpesaTransactions.setPartyB(mpesaConfiguration.getStkPushShortCode());
            mpesaTransactions.setAccountReference(internalStkPushRequest.getAccountReference());
            mpesaTransactions.setTransactionDesc("Freemium Subscription");
            mpesaTransactions.setTimeStamp(transactionTimestamp);
            mpesaTransactions.setMerchantRequestId(Objects.requireNonNull(stkPushResponse.getBody()).getMerchantRequestID());
            mpesaTransactions.setCheckoutRequestId(stkPushResponse.getBody().getCheckoutRequestID());
            mpesaTransactions.setResponseCode(stkPushResponse.getBody().getResponseCode());
            mpesaTransactions.setResponseDescription(stkPushResponse.getBody().getResponseDescription());
            mpesaTransactions.setSubscriptionPlan(internalStkPushRequest.getSubscriptionPlan());
            mpesaTransactionsRepository.save(mpesaTransactions);

            return stkPushResponse.getBody();
        } else {
            System.out.println("Request Failed");
            return stkPushResponse.getBody();
        }
    }

    @Override
    @Transactional
    public B2CTransactionResponse sendReferralCommission(String phoneNumber, String amount) {

        AccessTokenResponse accessTokenResponse = getB2CAccessToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        httpHeaders.setCacheControl("private, no-store, max-age=0");
        httpHeaders.setExpires(0);
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("QueueTimeOutURL", mpesaConfiguration.getB2cQueueTimeoutUrl());
        merchantToBeneficiary.put("Remarks", "Payment");
        merchantToBeneficiary.put("Amount", amount);
        merchantToBeneficiary.put("InitiatorName", mpesaConfiguration.getInitiatorName());
        merchantToBeneficiary.put("SecurityCredential", mpesaConfiguration.getInitiatorPassword());
        merchantToBeneficiary.put("Occassion", "Disbursement");
        merchantToBeneficiary.put("CommandID", "BusinessPayment");
        merchantToBeneficiary.put("PartyA", "3029499");
        merchantToBeneficiary.put("PartyB", phoneNumber);
        merchantToBeneficiary.put("ResultURL", mpesaConfiguration.getB2cResultUrl());

        HttpEntity<Map<String, Object>> merchantToBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        ResponseEntity<B2CTransactionResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                mpesaConfiguration.getB2cTransactionEndpoint(),
                merchantToBeneficiaryRequest, B2CTransactionResponse.class);

        MpesaTransactions mpesaTransactions = new MpesaTransactions();

        if (merchantToBeneficiaryRespones.getStatusCode() == HttpStatus.OK) {
            System.out.println("Merchant to Beneficiary Request Successful");
            log.info("Referrer has been credited");
            // update subscription table
            // also update user's record  set `is_subscribed` status to true, set `is_initial_subscription` status to false
            // set `is_renewal` status to true
//            customerRegistrationRepository.updateUsersSubscriptionStatus(subscibingCustomerAccNo);
            System.out.println(merchantToBeneficiaryRespones.getBody());
            mpesaTransactions.setTransactionType(CUSTOMER_PAYBILL_ONLINE);
            mpesaTransactions.setAmount(amount);
            mpesaTransactions.setPhoneNumber(phoneNumber);
            mpesaTransactions.setPartyA("3029499");
            mpesaTransactions.setPartyB("");
            mpesaTransactions.setAccountReference("");
            mpesaTransactions.setTransactionDesc("Referral Commission");
            mpesaTransactions.setTimeStamp(HelperUtility.getTransactionTimestamp());
            mpesaTransactions.setConversationID(Objects.requireNonNull(merchantToBeneficiaryRespones.getBody()).getConversationID());
            mpesaTransactions.setOriginatorConversationID(merchantToBeneficiaryRespones.getBody().getOriginatorConversationID());
            mpesaTransactions.setCheckoutRequestId("");
            mpesaTransactions.setResponseCode(merchantToBeneficiaryRespones.getBody().getResponseCode());
            mpesaTransactions.setResponseDescription(merchantToBeneficiaryRespones.getBody().getResponseDescription());
            mpesaTransactionsRepository.save(mpesaTransactions);
            return merchantToBeneficiaryRespones.getBody();
        } else {
            return merchantToBeneficiaryRespones.getBody();
        }
    }

    @Override
    @Transactional
    public B2CTransactionResponse creditUserForRewardedAds(RewardRequest rewardRequest) {
        CompletableFuture<B2CTransactionResponse> future = new CompletableFuture<>();

        AccessTokenResponse accessTokenResponse = getB2CAccessToken();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        httpHeaders.setCacheControl("private, no-store, max-age=0");
        httpHeaders.setExpires(0);
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("QueueTimeOutURL", mpesaConfiguration.getB2cQueueTimeoutUrl());
        merchantToBeneficiary.put("Remarks", "Payment");
        merchantToBeneficiary.put("Amount", rewardRequest.getAmount());
        merchantToBeneficiary.put("InitiatorName", mpesaConfiguration.getInitiatorName());
        merchantToBeneficiary.put("SecurityCredential", mpesaConfiguration.getInitiatorPassword());
        merchantToBeneficiary.put("Occassion", "Rewarded Ads Disbursement");
        merchantToBeneficiary.put("CommandID", "BusinessPayment");
        merchantToBeneficiary.put("PartyA", "3029499");
        merchantToBeneficiary.put("PartyB", rewardRequest.getPhoneNumber());
        merchantToBeneficiary.put("ResultURL", mpesaConfiguration.getRewardedAdsResultUrl());

        HttpEntity<Map<String, Object>> creditBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        ResponseEntity<B2CTransactionResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                mpesaConfiguration.getB2cTransactionEndpoint(),
                creditBeneficiaryRequest, B2CTransactionResponse.class);


        HttpStatus status = merchantToBeneficiaryRespones.getStatusCode();

        RewardedAdsTransactionsAttempts mpesaTransactions = new RewardedAdsTransactionsAttempts();
        if (status == HttpStatus.OK) {
            future.complete(merchantToBeneficiaryRespones.getBody());
            mpesaTransactions.setTransactionType(CUSTOMER_PAYBILL_ONLINE);
            mpesaTransactions.setAmount(rewardRequest.getAmount());
            mpesaTransactions.setAdvertType(rewardRequest.getAdType());
            mpesaTransactions.setAdvertId(Long.valueOf(rewardRequest.getAdvertId()));
            mpesaTransactions.setPhoneNumber(rewardRequest.getPhoneNumber());
            mpesaTransactions.setPartyA(mpesaConfiguration.getB2cShortCode());
            mpesaTransactions.setPartyB(mpesaConfiguration.getStkPushShortCode());
            mpesaTransactions.setAccountReference("");
            mpesaTransactions.setTransactionDesc("Rewarded Ads");
            mpesaTransactions.setTimeStamp(HelperUtility.getTransactionTimestamp());
            mpesaTransactions.setConversationID(Objects.requireNonNull(merchantToBeneficiaryRespones.getBody()).getConversationID());
            mpesaTransactions.setOriginatorConversationID(merchantToBeneficiaryRespones.getBody().getOriginatorConversationID());
            mpesaTransactions.setCheckoutRequestId("");
            mpesaTransactions.setResponseCode(merchantToBeneficiaryRespones.getBody().getResponseCode());
            mpesaTransactions.setResponseDescription(merchantToBeneficiaryRespones.getBody().getResponseDescription());
            rewardedAdsTransactionsAttemptsRepository.save(mpesaTransactions);
        } else {
            return merchantToBeneficiaryRespones.getBody();
        }

        return merchantToBeneficiaryRespones.getBody();
    }


    public B2CTransactionResponse sendMoney(String phoneNumber, int amount) {
        CompletableFuture<B2CTransactionResponse> future = new CompletableFuture<>();

        AccessTokenResponse accessTokenResponse = getB2CAccessToken();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        httpHeaders.setCacheControl("private, no-store, max-age=0");
        httpHeaders.setExpires(0);
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("QueueTimeOutURL", mpesaConfiguration.getB2cQueueTimeoutUrl());
        merchantToBeneficiary.put("Remarks", "Payment");
        merchantToBeneficiary.put("Amount", amount);
        merchantToBeneficiary.put("InitiatorName", mpesaConfiguration.getInitiatorName());
        merchantToBeneficiary.put("SecurityCredential", mpesaConfiguration.getInitiatorPassword());
        merchantToBeneficiary.put("Occasion", "Rewarded Ads Disbursement");
        merchantToBeneficiary.put("CommandID", "BusinessPayment");
        merchantToBeneficiary.put("PartyA", "3029499");
        merchantToBeneficiary.put("PartyB", phoneNumber);
        merchantToBeneficiary.put("ResultURL", mpesaConfiguration.getRewardedAdsResultUrl());

        HttpEntity<Map<String, Object>> creditBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        ResponseEntity<B2CTransactionResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                mpesaConfiguration.getB2cTransactionEndpoint(),
                creditBeneficiaryRequest, B2CTransactionResponse.class);


        HttpStatus status = merchantToBeneficiaryRespones.getStatusCode();

        if (status == HttpStatus.OK) {
            future.complete(merchantToBeneficiaryRespones.getBody());
        } else {
            return merchantToBeneficiaryRespones.getBody();
        }
        return merchantToBeneficiaryRespones.getBody();
    }


    @Override
    @Transactional
    public CheckAccountBalanceResponse checkAccountBalance() {
        CheckAccountBalanceRequest checkAccountBalanceRequest = new CheckAccountBalanceRequest();
        checkAccountBalanceRequest.setInitiator(mpesaConfiguration.getInitiatorName());
        checkAccountBalanceRequest.setCommandID(ACCOUNT_BALANCE_COMMAND);
        checkAccountBalanceRequest.setSecurityCredential(mpesaConfiguration.getInitiatorPassword());
        checkAccountBalanceRequest.setPartyA(mpesaConfiguration.getB2cShortCode());
        checkAccountBalanceRequest.setIdentifierType(SHORT_CODE_IDENTIFIER);
        checkAccountBalanceRequest.setRemarks("Check Account Balance");
        checkAccountBalanceRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        checkAccountBalanceRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());

        AccessTokenResponse accessTokenResponse = getAccessToken();

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(checkAccountBalanceRequest)));

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getCheckAccountBalanceUrl())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            // use Jackson to Decode the ResponseBody ...

            return objectMapper.readValue(response.body().string(), CheckAccountBalanceResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not fetch the account balance -> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    @Override
    @Async
    public B2CTransactionResponse creditMerchantForRedeemedVouchers(
            String voucherCode,
            String merchantPhoneNumber,
            String amount
    ) {
        CompletableFuture<B2CTransactionResponse> future = new CompletableFuture<>();

        AccessTokenResponse accessTokenResponse = getB2CAccessToken();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        httpHeaders.setCacheControl("private, no-store, max-age=0");
        httpHeaders.setExpires(0);
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("QueueTimeOutURL", mpesaConfiguration.getB2cQueueTimeoutUrl());
        merchantToBeneficiary.put("Remarks", "Payment");
        merchantToBeneficiary.put("Amount", amount);
        merchantToBeneficiary.put("InitiatorName", mpesaConfiguration.getInitiatorName());
        merchantToBeneficiary.put("SecurityCredential", mpesaConfiguration.getInitiatorPassword());
        merchantToBeneficiary.put("Occassion", "Rewarded Ads Disbursement");
        merchantToBeneficiary.put("CommandID", "BusinessPayment");
        merchantToBeneficiary.put("PartyA", "3029499");
        merchantToBeneficiary.put("PartyB", merchantPhoneNumber);
        merchantToBeneficiary.put("ResultURL", mpesaConfiguration.getRewardedAdsResultUrl());

        HttpEntity<Map<String, Object>> creditBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        ResponseEntity<B2CTransactionResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                mpesaConfiguration.getB2cTransactionEndpoint(),
                creditBeneficiaryRequest, B2CTransactionResponse.class);


        HttpStatus status = merchantToBeneficiaryRespones.getStatusCode();

        RewardedAdsTransactionsAttempts mpesaTransactions = new RewardedAdsTransactionsAttempts();
        if (status == HttpStatus.OK) {
            future.complete(merchantToBeneficiaryRespones.getBody());
            rewardedAdsTransactionsAttemptsRepository.save(mpesaTransactions);
        } else {

            return merchantToBeneficiaryRespones.getBody();
        }

        return merchantToBeneficiaryRespones.getBody();
    }

}
