package com.teleeza.wallet.teleeza.subscription.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.entity.BillsEntity;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.repository.BillPaymentRepository;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.response.MerchantToBeneficiaryResponse;
import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.service.MerchantToBeneficiarryApi;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.subscription.dtos.requests.SubscriptionDto;
import com.teleeza.wallet.teleeza.subscription.dtos.responses.SubscriptionResponse;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.AllTransactionsRepository;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
@Component
@CacheConfig(cacheNames = {"subscription"})
public class FreemiumSubscriptionService implements FreemiumService {
    private static final Logger logger = LoggerFactory.getLogger(FreemiumSubscriptionService.class);
    @Autowired
    private SasaPayApi sasaPayApi;
    @Autowired
    private MerchantToBeneficiarryApi merchantToBeneficiarryApi;
    private final SasaPayConfig sasaPayConfig;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private BillPaymentRepository billPaymentRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    private final OkHttpClient okHttpClient;
    @Autowired
    private AllTransactionsRepository allTransactionsRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    private final ObjectMapper objectMapper;
    private final KokotoaConfig kokotoaConfig;

    public FreemiumSubscriptionService(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper, KokotoaConfig kokotoaConfig) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.kokotoaConfig = kokotoaConfig;
    }

    // Send SMS notification to uses > Advanta
//    public void sendSmsNotification(String message, String mobile) {
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        Map<String, Object> notification = new HashMap<>();
//        notification.put("apikey", "b19a45d9e8f7a028d9afb7db0ec2cbe8");
//        notification.put("partnerID", "4555");
//        notification.put("message", message);
//        notification.put("shortcode", "TELEEZA");
//        notification.put("mobile", mobile);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(notification, httpHeaders);
//        ResponseEntity<String> response = restTemplate.postForEntity(
//                "https://quicksms.advantasms.com/api/services/sendsms/",
//                entity, String.class);
//
//        log.info("get mobile response{}", notification.get("mobile"));
//        // check response
//        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
//            System.out.println("Request Successful");
//            System.out.println(response.getBody());
//        } else {
//            System.out.println("Request Failed");
//            System.out.println(response.getStatusCode());
//        }
//    }

    // update transactions to kokotoa
    public String updateKokotoa(String name, String amount, String phone, String beneficiaryAccountNumber, String
            description) {
        //update record to kokotoa
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("amount", amount);
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("transactionFee","0");
        map.put("beneficiaryAccount", beneficiaryAccountNumber);

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
            return "Recorded Successful";
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            return "Failed";
        }
    }

    @Override
    public SubscriptionResponse freemiumSubscription(SubscriptionDto internalSubscriptionRequest) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(sasaPayApi.getAccessToken().getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", internalSubscriptionRequest.getBeneficiaryAccNo());
        merchantToBeneficiary.put("SasaPayBillNumber", internalSubscriptionRequest.getPayBillNo());
        merchantToBeneficiary.put("Amount", internalSubscriptionRequest.getAmount());
        merchantToBeneficiary.put("MerchantCode", "669994");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("Reason", 0);
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getFreemiumValidationCallBack());

        HttpEntity<Map<String, Object>> subscriptionRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        ResponseEntity<SubscriptionResponse> subscriptionResponse = restTemplate.postForEntity(
                sasaPayConfig.getBillPaymentEndpoint(),
                subscriptionRequest, SubscriptionResponse.class);

        if (subscriptionResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Subscription Request Successful");

            BillsEntity billsEntity = new BillsEntity();
            billsEntity.setPayBillNumber(String.valueOf(internalSubscriptionRequest.getPayBillNo()));
            billsEntity.setBeneficiaryAccountNumber(internalSubscriptionRequest.getBeneficiaryAccNo());
            billsEntity.setMerchantCode(sasaPayConfig.getMerchantCode());
            billsEntity.setAmount(String.valueOf(internalSubscriptionRequest.getAmount()));
            billsEntity.setBillRefNumber(subscriptionResponse.getBody().getMerchantReference());
            billsEntity.setTransactionReference(subscriptionResponse.getBody().getMerchantReference());
            billsEntity.setReferenceNumber(subscriptionResponse.getBody().getReferenceNumber());
            billsEntity.setStatusCode(subscriptionResponse.getBody().getStatusCode());
            billsEntity.setReason("Freemium Subscription");
            billsEntity.setMessage(subscriptionResponse.getBody().getMessage());
            billPaymentRepository.save(billsEntity);
            // Save transaction request and response to all_transactions `attempts table`
//            TransactionsEntity transactionsEntity = new TransactionsEntity();
//            transactionsEntity.setSenderBeneficiaryAccNumber(internalSubscriptionRequest.getBeneficiaryAccNo());
//            transactionsEntity.setAmount(Double.valueOf(internalSubscriptionRequest.getAmount()));
//            transactionsEntity.setMerchantCode(sasaPayConfig.getMerchantCode());
//            transactionsEntity.setResultCode(c2cResponse.getBody().getStatusCode());
//            transactionsEntity.setResultDesc(c2cResponse.getBody().getMessage());
//            transactionsEntity.setTransactionFee(0);
//            transactionsEntity.setReason("Freemium Subscription");
//            transactionsEntity.setRecipientBeneficiaryAccNumber(String.valueOf(internalSubscriptionRequest.getPayBillNo()));
//            transactionsEntity.setMerchantTransactionRef(Objects.requireNonNull(c2cResponse.getBody()).getMerchantReference());
//            transactionsEntity.setTransactionReference(c2cResponse.getBody().getMerchantReference());
//            allTransactionsRepository.save(transactionsEntity);

//            System.out.println(c2cResponse.getBody());
//            SubscriptionEntity subscription = subscriptionRepository.findByMerchantReference(c2cResponse.getBody().getMerchantReference());
//            logger.info("==== SUBSCRIPTION ======");
//            logger.info("Subscription Body {}", subscription);
            return subscriptionResponse.getBody();
        } else {
            return subscriptionResponse.getBody();
        }
    }

    @Override
    public MerchantToBeneficiaryResponse sendSubscriptionDiscount(String accountNumber, Integer amount) {

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(merchantToBeneficiarryApi.getAccessToken().getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", accountNumber);
        merchantToBeneficiary.put("Amount", amount);
        merchantToBeneficiary.put("SenderMerchantCode", "122122");
        merchantToBeneficiary.put("ReceiverMerchantCode", "669994");
        merchantToBeneficiary.put("Reason", "Referral Commission");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getMerchantToBeneficaryDiscountCallBack());

        HttpEntity<Map<String, Object>> merchantToBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        logger.info("Merchant To Benefiary Request {}", merchantToBeneficiaryRequest);
        ResponseEntity<MerchantToBeneficiaryResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                "https://api.sasapay.app/api/v1/payments/b2c/beneficiary/",
                merchantToBeneficiaryRequest, MerchantToBeneficiaryResponse.class);

        if (merchantToBeneficiaryRespones.getStatusCode() == HttpStatus.OK) {
            System.out.println("Merchant to Beneficiary Request Successful");
            logger.info("Referrer has been credited");
            logger.info("Referrer has been credited" + merchantToBeneficiaryRespones.getBody());
            // update subscription table
            // also update user's record  set `is_subscribed` status to true, set `is_initial_subscription` status to false
            // set `is_renewal` status to true
//            customerRegistrationRepository.updateUsersSubscriptionStatus(subscibingCustomerAccNo);
            System.out.println(merchantToBeneficiaryRespones.getBody());
            // TODO: save request and response in `all_transactions` table
            return merchantToBeneficiaryRespones.getBody();
        } else {
            return merchantToBeneficiaryRespones.getBody();
        }
    }

    @Override
    public MerchantToBeneficiaryResponse sendReferralCommission(String accountNumber, Integer amount) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(merchantToBeneficiarryApi.getAccessToken().getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", accountNumber);
        merchantToBeneficiary.put("Amount", amount);
        merchantToBeneficiary.put("SenderMerchantCode", "122122");
        merchantToBeneficiary.put("ReceiverMerchantCode", "669994");
        merchantToBeneficiary.put("Reason", "Referral Commission");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getMerchantToBeneficiarryCallBack());

        HttpEntity<Map<String, Object>> merchantToBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, headers);
        logger.info("Merchant To Benefiary Request {}", merchantToBeneficiaryRequest);
        ResponseEntity<MerchantToBeneficiaryResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                "https://api.sasapay.app/api/v1/payments/b2c/beneficiary/",
                merchantToBeneficiaryRequest, MerchantToBeneficiaryResponse.class);

        if (merchantToBeneficiaryRespones.getStatusCode() == HttpStatus.OK) {
            System.out.println("Merchant to Beneficiary Request Successful");
            logger.info("Referrer has been credited");
            logger.info("Referrer has been credited" + merchantToBeneficiaryRespones.getBody());
            // update subscription table
            // also update user's record  set `is_subscribed` status to true, set `is_initial_subscription` status to false
            // set `is_renewal` status to true
//            customerRegistrationRepository.updateUsersSubscriptionStatus(subscibingCustomerAccNo);
            System.out.println(merchantToBeneficiaryRespones.getBody());
            // TODO: save request and response in `all_transactions` table
            return merchantToBeneficiaryRespones.getBody();
        } else {
            return merchantToBeneficiaryRespones.getBody();
        }
    }

    @Override
    public MerchantToBeneficiaryResponse sendCashback(String accountNumber, Integer amount) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(merchantToBeneficiarryApi.getAccessToken().getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", accountNumber);
        merchantToBeneficiary.put("Amount", amount);
        merchantToBeneficiary.put("SenderMerchantCode", "122122");
        merchantToBeneficiary.put("ReceiverMerchantCode", "669994");
        merchantToBeneficiary.put("Reason", "Referral Commission");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getMerchantToBeneficaryCashbackCallBack());

        HttpEntity<Map<String, Object>> merchantToBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        logger.info("Merchant To Benefiary Request {}", merchantToBeneficiaryRequest);
        ResponseEntity<MerchantToBeneficiaryResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                "https://api.sasapay.app/api/v1/payments/b2c/beneficiary/",
                merchantToBeneficiaryRequest, MerchantToBeneficiaryResponse.class);

        if (merchantToBeneficiaryRespones.getStatusCode() == HttpStatus.OK) {
            System.out.println("Merchant to Beneficiary Request Successful");
            logger.info("Referrer has been credited");
            logger.info("Referrer has been credited" + merchantToBeneficiaryRespones.getBody());
            // update subscription table
            // also update user's record  set `is_subscribed` status to true, set `is_initial_subscription` status to false
            // set `is_renewal` status to true
//            customerRegistrationRepository.updateUsersSubscriptionStatus(subscibingCustomerAccNo);
            System.out.println(merchantToBeneficiaryRespones.getBody());
            // TODO: save request and response in `all_transactions` table
            return merchantToBeneficiaryRespones.getBody();
        } else {
            return merchantToBeneficiaryRespones.getBody();
        }
    }

    @Override
    public MerchantToBeneficiaryResponse sendResidualIncome(String accountNumber, Integer amount) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(merchantToBeneficiarryApi.getAccessToken().getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", accountNumber);
        merchantToBeneficiary.put("Amount", amount);
        merchantToBeneficiary.put("SenderMerchantCode", "122122");
        merchantToBeneficiary.put("ReceiverMerchantCode", "669994");
        merchantToBeneficiary.put("Reason", "Referral Commission");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getMerchantToBeneficaryResidualCallBack());

        HttpEntity<Map<String, Object>> merchantToBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        logger.info("Merchant To Benefiary Request {}", merchantToBeneficiaryRequest);
        ResponseEntity<MerchantToBeneficiaryResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                "https://api.sasapay.app/api/v1/payments/b2c/beneficiary/",
                merchantToBeneficiaryRequest, MerchantToBeneficiaryResponse.class);

        if (merchantToBeneficiaryRespones.getStatusCode() == HttpStatus.OK) {
            System.out.println("Merchant to Beneficiary Request Successful");
            logger.info("Referrer has been credited");
            logger.info("Referrer has been credited" + merchantToBeneficiaryRespones.getBody());
            // update subscription table
            // also update user's record  set `is_subscribed` status to true, set `is_initial_subscription` status to false
            // set `is_renewal` status to true
//            customerRegistrationRepository.updateUsersSubscriptionStatus(subscibingCustomerAccNo);
            System.out.println(merchantToBeneficiaryRespones.getBody());
            // TODO: save request and response in `all_transactions` table
            return merchantToBeneficiaryRespones.getBody();
        } else {
            return merchantToBeneficiaryRespones.getBody();
        }
    }

    public String extendSubscription(SubscriptionDto internalSubscriptionRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders payBillsHeaders = new HttpHeaders();
        payBillsHeaders.setContentType(MediaType.APPLICATION_JSON);
        payBillsHeaders.setBearerAuth(sasaPayApi.getAccessToken().getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BillRefNumber", HelperUtility.getBillRefNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", internalSubscriptionRequest.getBeneficiaryAccNo());
        merchantToBeneficiary.put("SasaPayBillNumber", internalSubscriptionRequest.getPayBillNo());
        merchantToBeneficiary.put("Amount", internalSubscriptionRequest.getAmount());
        merchantToBeneficiary.put("MerchantCode", "669994");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("Reason", "Pay Bill");
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getSubscriptionExtensionValidation());

        HttpEntity<Map<String, Object>> commission = new HttpEntity<>(merchantToBeneficiary, payBillsHeaders);
        ResponseEntity<SubscriptionResponse> commissionResponse = restTemplate.postForEntity(
                sasaPayConfig.getBillPaymentEndpoint(),
                commission, SubscriptionResponse.class);

        if (commissionResponse.getStatusCode() == HttpStatus.CREATED || commissionResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful");
            // find get user from the database, use the refferedbyCode to identity their referee and credit them
            System.out.println(commissionResponse.getBody());
            // TODO: save request and response in `all_transactions` table
        } else {
            // TODO: save request and response in `all_transactions` table
            System.out.println("Request Failed");
            System.out.println(commissionResponse.getStatusCode());
        }
        return "Subscription extended";
    }
}
