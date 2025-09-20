package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.controller;

import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.MerchantToBeneficiarryAuthResponse;
import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.request.MerchantToBeneficiarryAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.service.MerchantToBeneficiarryApi;
import com.teleeza.wallet.teleeza.notification.model.dtos.requests.PushNotificationRequest;
import com.teleeza.wallet.teleeza.notification.service.PushNotificationService;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TopPerfomers;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/v1/merchant-to-beneficiarry")
@Slf4j
public class MerchantToBeneficiarryController {
    private final MerchantToBeneficiarryApi merchantToBeneficiarryApi;
    private final TransactionsRepository transactionsRepository;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final KokotoaConfig kokotoaConfig;

    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PushNotificationService pushNotificationService;

    public MerchantToBeneficiarryController(
            MerchantToBeneficiarryApi merchantToBeneficiarryApi,
            TransactionsRepository transactionsRepository,
            CustomerRegistrationRepository customerRegistrationRepository,
            KokotoaConfig kokotoaConfig) {
        this.merchantToBeneficiarryApi = merchantToBeneficiarryApi;
        this.transactionsRepository = transactionsRepository;
        this.customerRegistrationRepository = customerRegistrationRepository;

        this.kokotoaConfig = kokotoaConfig;
    }

    @GetMapping(path = "/token", produces = "application/json")
    public ResponseEntity<MerchantToBeneficiarryAuthResponse> getAccessToken() {
        return ResponseEntity.ok(merchantToBeneficiarryApi.getAccessToken());
    }

    @GetMapping(path = "/top-earners")
    public Map<String, List<TopPerfomers>> getTopEarners() {
        Map<String, List<TopPerfomers>> response = new HashMap<>();
        response.put("topEarners", customerRegistrationRepository.getTopEarners());
        return response;
    }

    @PostMapping(path = "/subscription-discount/validation")
    public ResponseEntity<MerchantToBeneficiarryAsyncRequest> validateSubscriptionDiscount(
            @RequestBody MerchantToBeneficiarryAsyncRequest merchantToBeneficiarryAsyncRequest
    ) {

        // get beneficiary details from customer table
        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        String amountCreditedStr = merchantToBeneficiarryAsyncRequest.getTransAmount();
        BigInteger amountCredited = new BigDecimal(amountCreditedStr).toBigInteger();

        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
        transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
        transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
        transactions.setMerchantAccountBalance("");
        transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
        transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setDestinationChannel("0");
        transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setSourceChannel("SasaPay");
        transactions.setSasaPayTransactionId("");
        transactions.setRecipientName(customerEntity.getDisplayName());
        transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
        transactions.setReason("Subscription Discount");
        transactions.setSenderName("Teleeza Africa Ltd");
        transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setSenderAccountNumber("122122");
        transactions.setIsTransactionType(true);
        transactionsRepository.save(transactions);

        PushNotificationRequest notificationRequest = new PushNotificationRequest();
        notificationRequest.setTitle("Teleeza Wallet");
        notificationRequest.setMessage("Hi, "+customerEntity.getFirstName()+" Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as Cashback. Thank you for choosing Teleeza.");
        notificationRequest.setToken(customerEntity.getFcmToken());
        notificationRequest.setTopic("Send Money");

        pushNotificationService.sendPushNotificationToToken(notificationRequest);

        // update record on Kokotoa
        restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Welcome Bonus");
        map.put("description", "Receive Ksh. "
                + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as referral discount");
        map.put("amount", amountCredited);
        map.put("category", "Income");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful >> Transaction Saved On Kokotoa");
            System.out.println("Request Successful");
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(merchantToBeneficiarryAsyncRequest);
    }

    @PostMapping("subscription-cashback/validation")
    public ResponseEntity<MerchantToBeneficiarryAsyncRequest> validateCashBack(@RequestBody MerchantToBeneficiarryAsyncRequest merchantToBeneficiarryAsyncRequest){
        // get beneficiary details from customer table
        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        String amountCreditedStr = merchantToBeneficiarryAsyncRequest.getTransAmount();
        BigInteger amountCredited = new BigDecimal(amountCreditedStr).toBigInteger();

        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
        transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
        transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
        transactions.setMerchantAccountBalance("");
        transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
        transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setDestinationChannel("0");
        transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setSourceChannel("SasaPay");
        transactions.setSasaPayTransactionId("");
        transactions.setRecipientName(customerEntity.getDisplayName());
        transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
        transactions.setReason("Cashback");
        transactions.setSenderName("Teleeza Africa Ltd");
        transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setSenderAccountNumber("122122");
        transactions.setIsTransactionType(true);
        transactionsRepository.save(transactions);

        PushNotificationRequest notificationRequest = new PushNotificationRequest();
        notificationRequest.setTitle("Teleeza Wallet");
        notificationRequest.setMessage("Hi, "+customerEntity.getFirstName()+" Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as Cashback. Thank you for choosing Teleeza.");
        notificationRequest.setToken(customerEntity.getFcmToken());
        notificationRequest.setTopic("Send Money");

        pushNotificationService.sendPushNotificationToToken(notificationRequest);

        // update record on Kokotoa
        restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Welcome Bonus");
        map.put("description", "Receive Ksh. "
                + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as referral discount");
        map.put("amount", amountCredited);
        map.put("category", "Income");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful >> Transaction Saved On Kokotoa");
            System.out.println("Request Successful");
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(merchantToBeneficiarryAsyncRequest);
    }

    @PostMapping("residual-income/validation")
    public ResponseEntity<MerchantToBeneficiarryAsyncRequest> validateResidaulIncome(@RequestBody MerchantToBeneficiarryAsyncRequest merchantToBeneficiarryAsyncRequest){
        // get beneficiary details from customer table
        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        String amountCreditedStr = merchantToBeneficiarryAsyncRequest.getTransAmount();
        BigInteger amountCredited = new BigDecimal(amountCreditedStr).toBigInteger();

        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
        transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
        transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
        transactions.setMerchantAccountBalance("");
        transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
        transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setDestinationChannel("0");
        transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setSourceChannel("SasaPay");
        transactions.setSasaPayTransactionId("");
        transactions.setRecipientName(customerEntity.getDisplayName());
        transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
        transactions.setReason("Residual Income");
        transactions.setSenderName("Teleeza Africa Ltd");
        transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setSenderAccountNumber("122122");
        transactions.setIsTransactionType(true);
        transactionsRepository.save(transactions);

        PushNotificationRequest notificationRequest = new PushNotificationRequest();
        notificationRequest.setTitle("Teleeza Wallet");
        notificationRequest.setMessage("Hi, "+customerEntity.getFirstName()+" Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as Cashback. Thank you for choosing Teleeza.");
        notificationRequest.setToken(customerEntity.getFcmToken());
        notificationRequest.setTopic("Send Money");

        pushNotificationService.sendPushNotificationToToken(notificationRequest);

        // update record on Kokotoa
        restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Welcome Bonus");
        map.put("description", "Receive Ksh. "
                + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as referral discount");
        map.put("amount", amountCredited);
        map.put("category", "Income");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful >> Transaction Saved On Kokotoa");
            System.out.println("Request Successful");
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(merchantToBeneficiarryAsyncRequest);
    }


    @PostMapping("/validation")
//    @ApiIgnore
    public ResponseEntity<MerchantToBeneficiarryAsyncRequest> validateMerchantToBeneiciarry(
            @RequestBody MerchantToBeneficiarryAsyncRequest merchantToBeneficiarryAsyncRequest
    ) {

        // get beneficiary details from customer table
        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        String amountCreditedStr = merchantToBeneficiarryAsyncRequest.getTransAmount();
        BigInteger amountCredited = new BigDecimal(amountCreditedStr).toBigInteger();

        if (Objects.equals(merchantToBeneficiarryAsyncRequest.getTransAmount(), "100.00")) {
            //save in transactions
            Transactions transactions = new Transactions();
            transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
            transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
            transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
            transactions.setMerchantAccountBalance("");
            transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setDestinationChannel("0");
            transactions.setSourceChannel("SasaPay");
            transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setSasaPayTransactionId("");
            transactions.setRecipientName(customerEntity.getDisplayName());
            transactions.setSenderName("Teleeza Africa Ltd");
            transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
            transactions.setReason("Referral Commission");
            transactions.setSenderAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setIsTransactionType(true);
            transactionsRepository.save(transactions);


            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Referral Commission");
            notificationRequest.setMessage("Hi "+customerEntity.getFirstName()+", Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as referral commission.Thank you for choosing Teleeza.");
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");

            // update record on Kokotoa
            Map<String, Object> map = new HashMap<>();
            map.put("name", "Referral Commission");
            map.put("description", "Receive Ksh. "
                    + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as referral commission");
            map.put("amount", amountCredited);
            map.put("category", "Income");
            map.put("date", String.valueOf(LocalDate.now()));
            map.put("mobile", phone);
            map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

            ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                    kokotoaConfig.getKokotoaApiEndpoint(),
                    map,
                    Void.class);

            if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
                log.info("Request Successful >> Transaction Saved On Kokotoa");
                System.out.println("Request Successful");
            } else {
                log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            }
        }
        if (Objects.equals(merchantToBeneficiarryAsyncRequest.getTransAmount(), "50.00")) {
            Transactions transactions = new Transactions();
            transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
            transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
            transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
            transactions.setMerchantAccountBalance("");
            transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setDestinationChannel("0");
            transactions.setSourceChannel("SasaPay");
            transactions.setSasaPayTransactionId("");
            transactions.setRecipientName("");
            transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
            transactions.setReason("Residual Income");
            transactions.setSenderAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setIsTransactionType(true);
            transactionsRepository.save(transactions);

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Residual Income");
            notificationRequest.setMessage("Hi "+customerEntity.getFirstName()+",Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as residual commission. Thank you for choosing Teleeza.");
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Residual Income");

            // update record on Kokotoa
            restTemplate = new RestTemplate();
            Map<String, Object> map = new HashMap<>();
            map.put("name", "Residual Commission");
            map.put("description", "Receive Ksh. " + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as residual commission");
            map.put("amount", amountCredited);
            map.put("category", "Income");
            map.put("date", String.valueOf(LocalDate.now()));
            map.put("mobile", phone);
            map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

            ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                    kokotoaConfig.getKokotoaApiEndpoint(),
                    map,
                    Void.class);

            if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
                log.info("Request Successful >> Transaction Saved On Kokotoa");
                System.out.println("Request Successful");
            } else {
                log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            }
        }

        if (Objects.equals(merchantToBeneficiarryAsyncRequest.getTransAmount(), "200.00")) {
            Transactions transactions = new Transactions();
            transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
            transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
            transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
            transactions.setMerchantAccountBalance("");
            transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setDestinationChannel("0");
            transactions.setSourceChannel("SasaPay");
            transactions.setSasaPayTransactionId("");
            transactions.setRecipientName("");
            transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
            transactions.setReason("Referral Commission");
            transactions.setSenderAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setIsTransactionType(true);
            transactionsRepository.save(transactions);


            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Teleeza Wallet");
            notificationRequest.setMessage("Hi "+customerEntity.getFirstName()+", Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as referral commission. Thank you for choosing Teleeza.");
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Residual Income");

            // update record on Kokotoa
            restTemplate = new RestTemplate();
            Map<String, Object> map = new HashMap<>();
            map.put("name", "Referral Commission");
            map.put("description", "Receive Ksh. " + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as referral commission");
            map.put("amount", amountCredited);
            map.put("category", "Income");
            map.put("date", String.valueOf(LocalDate.now()));
            map.put("mobile", phone);
            map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

            ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                    kokotoaConfig.getKokotoaApiEndpoint(),
                    map,
                    Void.class);

            if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
                log.info("Request Successful >> Transaction Saved On Kokotoa");
                System.out.println("Request Successful");
            } else {
                log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            }
        }

        if (Objects.equals(merchantToBeneficiarryAsyncRequest.getTransAmount(), "350.00")) {
            Transactions transactions = new Transactions();
            transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
            transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
            transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
            transactions.setMerchantAccountBalance("");
            transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setDestinationChannel("0");
            transactions.setSourceChannel("SasaPay");
            transactions.setSasaPayTransactionId("");
            transactions.setRecipientName("");
            transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
            transactions.setReason("Referral Commission");
            transactions.setSenderAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setIsTransactionType(true);
            transactionsRepository.save(transactions);


            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Referral Commission");
            notificationRequest.setMessage("Hi "+customerEntity.getFirstName()+" Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as welcome bonus. Thank you for choosing Teleeza.");
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");

            // update record on Kokotoa
            Map<String, Object> map = new HashMap<>();
            map.put("name", "Referral Income");
            map.put("description", "Receive Ksh. " + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as referral commission");
            map.put("amount", amountCredited);
            map.put("category", "Income");
            map.put("date", String.valueOf(LocalDate.now()));
            map.put("mobile", phone);
            map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

            ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                    kokotoaConfig.getKokotoaApiEndpoint(),
                    map,
                    Void.class);

            if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
                log.info("Request Successful >> Transaction Saved On Kokotoa");
                System.out.println("Request Successful");
            } else {
                log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            }
        }
        if (Objects.equals(merchantToBeneficiarryAsyncRequest.getTransAmount(), "650.00")) {
            Transactions transactions = new Transactions();
            transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
            transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
            transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
            transactions.setMerchantAccountBalance("");
            transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setDestinationChannel("0");
            transactions.setSourceChannel("SasaPay");
            transactions.setSasaPayTransactionId("");
            transactions.setRecipientName("");
            transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
            transactions.setReason("Referral Commission");
            transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setSenderAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setIsTransactionType(true);
            transactionsRepository.save(transactions);

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Referral Commission");
            notificationRequest.setMessage("Hi "+customerEntity.getFirstName() +", Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as referral commission.Thank you for choosing Teleeza.");
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");

            // update record on Kokotoa
            Map<String, Object> map = new HashMap<>();
            map.put("name", "Referral Commission");
            map.put("description", "Receive Ksh. " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " as referral commission");
            map.put("amount", amountCredited);
            map.put("category", "Income");
            map.put("date", String.valueOf(LocalDate.now()));
            map.put("mobile", phone);
            map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

            ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                    kokotoaConfig.getKokotoaApiEndpoint(),
                    map,
                    Void.class);

            if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
                log.info("Request Successful >> Transaction Saved On Kokotoa");
                System.out.println("Request Successful");
            } else {
                log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            }
        }
        if (Objects.equals(merchantToBeneficiarryAsyncRequest.getTransAmount(), "150.00")) {
            Transactions transactions = new Transactions();
            transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
            transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
            transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
            transactions.setMerchantAccountBalance("");
            transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setDestinationChannel("0");
            transactions.setSourceChannel("SasaPay");
            transactions.setSasaPayTransactionId("");
            transactions.setRecipientName("");
            transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
            transactions.setReason("Residual Income");
            transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setSenderAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setIsTransactionType(true);
            transactionsRepository.save(transactions);

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Referral Commission");
            notificationRequest.setMessage("Hi "+customerEntity.getFirstName()+", Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as referral commission");
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");

            // update record on Kokotoa
            Map<String, Object> map = new HashMap<>();
            map.put("name", "Referral Commission");
            map.put("description", "Receive Ksh. " + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as referral commission");
            map.put("amount", amountCredited);
            map.put("category", "Income");
            map.put("date", String.valueOf(LocalDate.now()));
            map.put("mobile", phone);
            map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());

            ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                    kokotoaConfig.getKokotoaApiEndpoint(),
                    map,
                    Void.class);

            if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
                log.info("Request Successful >> Transaction Saved On Kokotoa");
                System.out.println("Request Successful");
            } else {
                log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            }
        }

        if (merchantToBeneficiarryAsyncRequest.getTransAmount().equals("300.00")) {
            Transactions transactions = new Transactions();
            transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
            transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
            transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
            transactions.setMerchantAccountBalance("");
            transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setDestinationChannel("0");
            transactions.setSourceChannel("SasaPay");
            transactions.setSasaPayTransactionId("");
            transactions.setRecipientName("");
            transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
            transactions.setReason("Residual Income");
            transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setSenderAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setIsTransactionType(true);
            transactionsRepository.save(transactions);

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Residual Income");
            notificationRequest.setMessage("Hi "+customerEntity.getFirstName()+", Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as residual commission.Thank you for choosing Teleeza.");
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");

            // update record on Kokotoa
            Map<String, Object> map = new HashMap<>();
            map.put("name", "Residual Income");
            map.put("description", "Receive Ksh. " + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as referral commission");
            map.put("amount", amountCredited);
            map.put("category", "Income");
            map.put("date", String.valueOf(LocalDate.now()));
            map.put("mobile", phone);
            map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                    kokotoaConfig.getKokotoaApiEndpoint(),
                    map,
                    Void.class);

            if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
                log.info("Request Successful >> Transaction Saved On Kokotoa");
                System.out.println("Request Successful");
            } else {
                log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            }
        }

        if (merchantToBeneficiarryAsyncRequest.getTransAmount().equals("600.00")) {
            Transactions transactions = new Transactions();
            transactions.setMerchantRequestId(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setCheckoutRequestId(merchantToBeneficiarryAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(merchantToBeneficiarryAsyncRequest.getResultCode());
            transactions.setResultDesc(merchantToBeneficiarryAsyncRequest.getResultDesc());
            transactions.setMerchantCode(merchantToBeneficiarryAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(merchantToBeneficiarryAsyncRequest.getTransAmount());
            transactions.setMerchantAccountBalance("");
            transactions.setMerchantTransactionReference(merchantToBeneficiarryAsyncRequest.getMerchantRequestID());
            transactions.setTransactionDate(merchantToBeneficiarryAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setDestinationChannel("0");
            transactions.setSourceChannel("SasaPay");
            transactions.setSasaPayTransactionId("");
            transactions.setRecipientName("");
            transactions.setBeneficiaryAccNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setSenderMerchantCode(merchantToBeneficiarryAsyncRequest.getSenderMerchantCode());
            transactions.setReason("Residual Income");
            transactions.setSenderAccountNumber(merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            transactions.setIsTransactionType(true);
            transactionsRepository.save(transactions);


            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Residual Commission");
            notificationRequest.setMessage("Hi "+customerEntity.getFirstName()+", Confirmed " + merchantToBeneficiarryAsyncRequest.getMerchantRequestID() + " You have received Ksh " + merchantToBeneficiarryAsyncRequest.getTransAmount() + " from Teleeza Africa Ltd as residual commission. Thank you for choosing Teleeza.");
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");

            // update record on Kokotoa
            Map<String, Object> map = new HashMap<>();
            map.put("name", "Residual Income");
            map.put("description", "Receive Ksh. " + merchantToBeneficiarryAsyncRequest.getTransAmount() + "as residual commission");
            map.put("amount", amountCredited);
            map.put("category", "Income");
            map.put("date", String.valueOf(LocalDate.now()));
            map.put("mobile", phone);
            map.put("beneficiaryAccount", merchantToBeneficiarryAsyncRequest.getBeneficiaryAccountNumber());
            ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                    kokotoaConfig.getKokotoaApiEndpoint(),
                    map,
                    Void.class);

            if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
                log.info("Request Successful >> Transaction Saved On Kokotoa");
                System.out.println("Request Successful");
            } else {
                log.info("Request Failed {}", kokotoaResponse.getStatusCode());
            }
        }

        return ResponseEntity.ok(merchantToBeneficiarryAsyncRequest);
    }
}
