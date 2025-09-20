package com.teleeza.wallet.teleeza.subscription.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.repository.BillPaymentRepository;
import com.teleeza.wallet.teleeza.common.AcknowledgeResponse;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.notification.model.dtos.requests.PushNotificationRequest;
import com.teleeza.wallet.teleeza.notification.service.PushNotificationService;
import com.teleeza.wallet.teleeza.subscription.dtos.requests.SubscriptionAsyncRequest;
import com.teleeza.wallet.teleeza.subscription.dtos.requests.SubscriptionDto;
import com.teleeza.wallet.teleeza.subscription.dtos.responses.SubscriptionResponse;
import com.teleeza.wallet.teleeza.subscription.entity.SubscriptionEntity;
import com.teleeza.wallet.teleeza.subscription.repository.OrganisationRepository;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionRepository;
import com.teleeza.wallet.teleeza.subscription.service.FreemiumSubscriptionService;
import com.teleeza.wallet.teleeza.subscription.service.MelioraSubscriptionService;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/v1/teleeza-wallet")
public class SubscriptionController {
    private final ObjectMapper objectMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final AcknowledgeResponse acknowledgeResponse;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final TransactionsRepository transactionsRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FreemiumSubscriptionService freemiumSubscriptionService;
    @Autowired
    private AdvantaSmsApiImpl advantaSmsApiImpl;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private MelioraSubscriptionService melioraSubscriptionService;
    @Autowired
    private BillPaymentRepository billPaymentRepository;
    private final KokotoaConfig kokotoaConfig;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    public SubscriptionController(
            ObjectMapper objectMapper,
            SubscriptionRepository subscriptionRepository,
            AcknowledgeResponse acknowledgeResponse, CustomerRegistrationRepository customerRegistrationRepository,
            TransactionsRepository transactionsRepository, KokotoaConfig kokotoaConfig) {

        this.objectMapper = objectMapper;
        this.subscriptionRepository = subscriptionRepository;
        this.acknowledgeResponse = acknowledgeResponse;
        this.customerRegistrationRepository = customerRegistrationRepository;
        this.transactionsRepository = transactionsRepository;
        this.kokotoaConfig = kokotoaConfig;
    }

    @PostMapping("/freemium")
    public ResponseEntity<SubscriptionResponse> freemiumSubscription(
            @RequestBody SubscriptionDto subscriptionRequest
    ) throws JsonProcessingException {
        CustomerEntity customer =
                customerRegistrationRepository.findByCustomerAccountNumber(subscriptionRequest.getBeneficiaryAccNo());
        Boolean existsByReferralCode = customerRegistrationRepository.existsByReferralCode(subscriptionRequest.getReferredByCode());
        Boolean existsByOrganisationCode = organisationRepository.existsByOrganisationCode(subscriptionRequest.getReferredByCode());
        if (customer.getIsSubscribed().equals(true)) {
            freemiumSubscriptionService.extendSubscription(subscriptionRequest);
            if (existsByReferralCode.equals(true) || existsByOrganisationCode) {
                customer.setReferredByCode(subscriptionRequest.getReferredByCode());
                customerRegistrationRepository.save(customer);
                log.info("saved referralCode");
            } else {
                customerRegistrationRepository.save(customer);
            }
            SubscriptionResponse response = new SubscriptionResponse();
            response.setMerchantReference("");
            response.setReferenceNumber("");
            response.setMessage("Your subscription has been extended");
            response.setStatusCode("000");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (existsByReferralCode.equals(true) || existsByOrganisationCode) {
                customer.setReferredByCode(subscriptionRequest.getReferredByCode());
                customerRegistrationRepository.save(customer);
                log.info("saved referralCode");
            } else {
                customerRegistrationRepository.save(customer);
            }

            SubscriptionResponse subscription = freemiumSubscriptionService.freemiumSubscription(subscriptionRequest);

            String response = objectMapper.writeValueAsString(subscription);
            SubscriptionResponse subscribeResponse = objectMapper.readValue(response, SubscriptionResponse.class);

            return ResponseEntity.ok(subscription);
        }
    }

    @PostMapping(path = "/freemium-validation")
//    @ApiIgnore
    public CompletableFuture<AcknowledgeResponse> freemiumValidation(
            @RequestBody SubscriptionAsyncRequest subscriptionAsyncRequest
    ) {
        CustomerEntity subscribingCustomer = customerRegistrationRepository
                .findByCustomerAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());

        double transactedAmount = Double.parseDouble(subscriptionAsyncRequest.getTransactionAmount());
        int amountTransacted = (int) transactedAmount;
        String transactionFee = amountTransacted <= 49 ? "1" :
                amountTransacted >= 50 && amountTransacted <= 99 ? "2" :
                        amountTransacted >= 100 && amountTransacted <= 499 ? "5" :
                                amountTransacted >= 500 && amountTransacted <= 999 ? "10" :
                                        amountTransacted >= 1000 && amountTransacted <= 1499 ? "10" :
                                                amountTransacted >= 1500 && amountTransacted <= 3499 ? "15" :
                                                        amountTransacted >= 3500 && amountTransacted <= 4999 ? "20" :
                                                                amountTransacted >= 5000 && amountTransacted <= 7499 ? "35" :
                                                                        amountTransacted >= 75000 && amountTransacted <= 150000 ? "50" : "";


        String customerPhone = subscribingCustomer.getMobileNumber();
        String phone = "" + customerPhone.substring(1);
        // save transaction callback to database `validated transactions`
        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(subscriptionAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(subscriptionAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(subscriptionAsyncRequest.getResultCode());
        transactions.setResultDesc(subscriptionAsyncRequest.getResultDesc());
        transactions.setMerchantCode(subscriptionAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(subscriptionAsyncRequest.getTransactionAmount());
        transactions.setMerchantAccountBalance(subscriptionAsyncRequest.getMerchantAccountBalance());
        transactions.setMerchantTransactionReference(subscriptionAsyncRequest.getMerchantTransactionReference());
        transactions.setTransactionDate(subscriptionAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(subscriptionAsyncRequest.getRecipientAccountNumber());
        transactions.setDestinationChannel(subscriptionAsyncRequest.getDestinationChannel());
        transactions.setSourceChannel(subscriptionAsyncRequest.getSourceChannel());
        transactions.setSasaPayTransactionId(subscriptionAsyncRequest.getSasaPayTransactionID());
        transactions.setRecipientName(subscriptionAsyncRequest.getRecipientName());
        transactions.setReason("Freemium Subscription");
        transactions.setTransactionFee("0");
        transactions.setSenderName(subscribingCustomer.getDisplayName());
        transactions.setRecipientName(subscriptionAsyncRequest.getRecipientName());
        transactions.setBeneficiaryAccNumber(subscriptionAsyncRequest.getSenderAccountNumber());
        transactions.setSenderAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
        transactions.setIsTransactionType(false);
        transactionsRepository.save(transactions);

        String subAmount = subscriptionAsyncRequest.getTransactionAmount();
        BigInteger amountSub = new BigDecimal(subAmount).toBigInteger();


        CustomerEntity referringCustomer = customerRegistrationRepository.findUserByReferralCode(subscribingCustomer.getReferredByCode());
        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();

        if (subscriptionAsyncRequest.getTransactionAmount().equals("150.00")) {

            subscriptionEntity.setBeneficiaryAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
            subscriptionEntity.setStartTime(LocalDateTime.now());
            subscriptionEntity.setExpirationTime(LocalDateTime.now().plusDays(30));
            subscriptionEntity.setAmount(subscriptionAsyncRequest.getTransactionAmount());
            subscriptionEntity.setMerchantReference(subscriptionAsyncRequest.getMerchantRequestID());
            subscriptionEntity.setPlanName("Monthly");
            subscriptionEntity.setReferredByCode(subscribingCustomer.getReferredByCode());
            subscriptionEntity.setIsInitialSubscription(true);
            subscriptionEntity.setCredited(false);
            subscriptionEntity.setIsSubscriptionSatus(true);
            subscriptionEntity.setPlanId("1");
            subscriptionRepository.save(subscriptionEntity);

            subscribingCustomer.setIsSubscribed(true);
            subscribingCustomer.setIsRenewal(true);
            subscribingCustomer.setExpirationTime(subscriptionEntity.getExpirationTime());
            customerRegistrationRepository.save(subscribingCustomer);

            DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
            String ldtString = FOMATTER.format(subscriptionEntity.getExpirationTime());
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear "
                            + subscribingCustomer.getFirstName() +
                            ", thank you for subscribing to the Teleeza Freemium Package." +
                            " Your subscription starts today and will expire on " + ldtString,
                    phone
            );

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Teleeza Wallet");
            notificationRequest.setMessage("Dear  " + subscribingCustomer.getFirstName() +
                    ", thank your for subscribing to the Teleeza Freemium Package." +
                    " Your subscription starts today and will expire on " + ldtString);
            notificationRequest.setToken(subscribingCustomer.getFcmToken());
            notificationRequest.setTopic("Send Commission Discount");
            pushNotificationService.sendPushNotificationToToken(notificationRequest);

            //if a user subscribes with a referral code  credit the referring user Ksh 50 as commission and credit the
            // subscribing user Ksh 15 as a 6% subscription discount
            if (subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() != null) {
                // find the referring customer by the referral code(referredByCode) and get their account number
                // send the referring customer Ksh 50 as referral commission and the subscribing customer Ksh 15 as discount/cashback
//                freemiumSubscriptionService.sendCashback(subscribingCustomer.getCustomerAccountNumber(), 100);
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
                if (referringCustomer != null) {
                    if(referringCustomer.getMobileUserType().equals("Normal User")){
                        freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 50);
                    }
                    if(referringCustomer.getMobileUserType().equals("Agent")){
                        freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 65);
                    }
                    if(referringCustomer.getMobileUserType().equals("Super Agent")){
                        freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 80);
                    }

                }
                customerRegistrationRepository.save(subscribingCustomer);
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // if a user subscribes without a referral code , credit them a 6 % discount
            if (subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() == null) {
//                freemiumSubscriptionService.sendSubscriptionDiscount(subscribingCustomer.getCustomerAccountNumber(), 100);
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // on subsequent subscription with referredBy code credit the referrer Ksh 50 residual commission
            if (!subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() != null) {

//                if (referringCustomer != null) {
//                    freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 50);
//                }
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // on subsequent subscription without referredBy code update the subscribing user's subscription status to true
            if (!subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() == null) {
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // Save transaction record to Kokotoa
            freemiumSubscriptionService.updateKokotoa(
                    "Teleeza Freemium Subscription",
                    String.valueOf(amountSub),
                    phone, subscriptionAsyncRequest.getSenderAccountNumber(),
                    "Freemium Subscription"
            );

            melioraSubscriptionService.sendSubscriptionStatus(subscribingCustomer.getMobileNumber(), "MONTH", "1");
        }

        if (subscriptionAsyncRequest.getTransactionAmount().equals("400.00")) {
            subscriptionEntity.setBeneficiaryAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
            subscriptionEntity.setStartTime(LocalDateTime.now());
            subscriptionEntity.setExpirationTime(LocalDateTime.now().plusDays(90));
            subscriptionEntity.setAmount(subscriptionAsyncRequest.getTransactionAmount());
            subscriptionEntity.setMerchantReference(subscriptionAsyncRequest.getMerchantRequestID());
            subscriptionEntity.setPlanName("Quarterly");

            subscriptionEntity.setReferredByCode(subscribingCustomer.getReferredByCode());
            subscriptionEntity.setIsInitialSubscription(true);
            subscriptionEntity.setCredited(false);
            subscriptionEntity.setIsSubscriptionSatus(true);
            subscriptionEntity.setPlanId("3");
            subscriptionRepository.save(subscriptionEntity);

            subscribingCustomer.setIsSubscribed(true);
            subscribingCustomer.setIsRenewal(true);
            subscribingCustomer.setExpirationTime(subscriptionEntity.getExpirationTime());
            customerRegistrationRepository.save(subscribingCustomer);

            DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
            String ldtString = FOMATTER.format(subscriptionEntity.getExpirationTime());
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear "
                            + subscribingCustomer.getFirstName() +
                            ", thank you for subscribing to the Teleeza Freemium Package." +
                            " Your subscription starts today and will expire on " + ldtString,
                    phone
            );

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Teleeza Wallet");
            notificationRequest.setMessage("Dear  " + subscribingCustomer.getFirstName() +
                    ", thank your for subscribing to the Teleeza Freemium Package." +
                    " Your subscription starts today and will expire on " + ldtString);
            notificationRequest.setToken(subscribingCustomer.getFcmToken());
            notificationRequest.setTopic("Send Money");
            pushNotificationService.sendPushNotificationToToken(notificationRequest);

            //if a user subscribes with a referral code  credit the referring user Ksh 50 as commission and credit the
            // subscribing user Ksh 15 as a 6% subscription discount
            if (subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() != null) {

                // find the referring customer by the referral code(referredByCode) and get their account number
                // send the referring customer Ksh 50 as referral commission and the subscribing customer Ksh 50 as discount
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
                freemiumSubscriptionService.sendCashback(subscribingCustomer.getCustomerAccountNumber(), 150 );
                if (referringCustomer != null) {
                   if(referringCustomer.getMobileUserType().equals("Normal User")){
                       freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 50);
                   }
                    if(referringCustomer.getMobileUserType().equals("Agent")){
                        freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 65);
                    }
                    if(referringCustomer.getMobileUserType().equals("Super Agent")){
                        freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 80);
                    }
                }
                customerRegistrationRepository.save(subscribingCustomer);
            }

            // if a user subscribes without a referral code , credit them a 6 % discount
            if (subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() == null) {
//                freemiumSubscriptionService.sendSubscriptionDiscount(subscribingCustomer.getCustomerAccountNumber(), 150);
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // on subsequent subscription with referredBy code credit the referrer Ksh 15 residual commission
            if (!subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() != null) {
//                freemiumSubscriptionService.sendSubscriptionDiscount(subscribingCustomer.getCustomerAccountNumber(), 150);
//                if (referringCustomer != null) {
//                    freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 150);
//
//                }
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // on subsequent subscription without referredBy code update the subscribing user's subscription status to true
            if (!subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() == null) {
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
//                freemiumSubscriptionService.sendSubscriptionDiscount(subscribingCustomer.getCustomerAccountNumber(),150);
            }

            // Save transaction record to Kokotoa

            freemiumSubscriptionService.updateKokotoa(
                    "Teleeza Freemium Subscription",
                    String.valueOf(amountSub),
                    phone, subscriptionAsyncRequest.getSenderAccountNumber(),
                    "Freemium Subscription"
            );

            melioraSubscriptionService.sendSubscriptionStatus(subscribingCustomer.getMobileNumber(), "MONTH", "3");
        }

        if (subscriptionAsyncRequest.getTransactionAmount().equals("800.00")) {
            subscriptionEntity.setBeneficiaryAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
            subscriptionEntity.setStartTime(LocalDateTime.now());
            subscriptionEntity.setExpirationTime(LocalDateTime.now().plusDays(180));
            subscriptionEntity.setAmount(subscriptionAsyncRequest.getTransactionAmount());
            subscriptionEntity.setMerchantReference(subscriptionAsyncRequest.getMerchantRequestID());
            subscriptionEntity.setPlanName("Semi Annual");
            subscriptionEntity.setReferredByCode(subscribingCustomer.getReferredByCode());
            subscriptionEntity.setIsInitialSubscription(true);
            subscriptionEntity.setCredited(false);
            subscriptionEntity.setIsSubscriptionSatus(true);
            subscriptionEntity.setPlanId("3");
            subscriptionRepository.save(subscriptionEntity);

            subscribingCustomer.setIsSubscribed(true);
            subscribingCustomer.setIsRenewal(true);
            subscribingCustomer.setExpirationTime(subscriptionEntity.getExpirationTime());
            customerRegistrationRepository.save(subscribingCustomer);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
            String ldtString = dateTimeFormatter.format(subscriptionEntity.getExpirationTime());
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear "
                            + subscribingCustomer.getFirstName() +
                            ", thank you for subscribing to the Teleeza Freemium Package." +
                            " Your subscription starts today and will expire on " + ldtString,
                    phone
            );

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Teleeza Wallet");
            notificationRequest.setMessage("Dear  " + subscribingCustomer.getFirstName() +
                    ", thank your for subscribing to the Teleeza Freemium Package." +
                    " Your subscription starts today and will expire on " + ldtString);
            notificationRequest.setToken(subscribingCustomer.getFcmToken());
            notificationRequest.setTopic("Send Money");
            pushNotificationService.sendPushNotificationToToken(notificationRequest);

            //if a user subscribes with a referral code  credit the referring user Ksh 50 as commission and credit the
            // subscribing user Ksh 100 as a 6% subscription discount
            if (subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() != null) {

                // find the referring customer by the referral code(referredByCode) and get their account number
                // send the referring customer Ksh 50 as referral commission and the subscribing customer Ksh 100 as discount

                customerRegistrationRepository.save(subscribingCustomer);

//                freemiumSubscriptionService.sendSubscriptionDiscount(subscribingCustomer.getCustomerAccountNumber(), 15);
                freemiumSubscriptionService.sendCashback(subscribingCustomer.getCustomerAccountNumber(), 200);
                if (referringCustomer != null) {
                    if(referringCustomer.getMobileUserType().equals("Normal User")){
                        freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 50);
                    }

                    if(referringCustomer.getMobileUserType().equals("Agent")){
                        freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 65);
                    }
                    if(referringCustomer.getMobileUserType().equals("Super Agent")){
                        freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 80);
                    }

                }
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // if a user subscribes without a referral code , credit them a 6 % discount
            if (subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() == null) {
//                freemiumSubscriptionService.sendCashback(subscribingCustomer.getCustomerAccountNumber(), 200);
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }


            if (!subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() != null) {
//                freemiumSubscriptionService.sendSubscriptionDiscount(subscribingCustomer.getCustomerAccountNumber(), 200);
//                if (referringCustomer != null) {
//                    freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 300);
//
//                }
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // on subsequent subscription without referredBy code update the subscribing user's subscription status to true
            if (!subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() == null) {
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
//                freemiumSubscriptionService.sendSubscriptionDiscount(subscribingCustomer.getCustomerAccountNumber(), 200);
            }

            // Save transaction record to Kokotoa
            freemiumSubscriptionService.updateKokotoa(
                    "Teleeza Freemium Subscription",
                    String.valueOf(amountSub),
                    phone, subscriptionAsyncRequest.getSenderAccountNumber(),
                    "Freemium Subscription"
            );

            melioraSubscriptionService.sendSubscriptionStatus(subscribingCustomer.getMobileNumber(), "MONTH", "6");
        }

        if (subscriptionAsyncRequest.getTransactionAmount().equals("1650.00")) {
            subscriptionEntity.setBeneficiaryAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
            subscriptionEntity.setStartTime(LocalDateTime.now());

            subscriptionEntity.setExpirationTime(LocalDateTime.now().plusDays(365));
            subscriptionEntity.setAmount(subscriptionAsyncRequest.getTransactionAmount());
            subscriptionEntity.setMerchantReference(subscriptionAsyncRequest.getMerchantRequestID());
            subscriptionEntity.setPlanName("Monthly");

            subscriptionEntity.setReferredByCode(subscribingCustomer.getReferredByCode());
            subscriptionEntity.setIsInitialSubscription(true);
            subscriptionEntity.setCredited(false);
            subscriptionEntity.setIsSubscriptionSatus(true);
            subscriptionEntity.setPlanId("3");
            subscriptionRepository.save(subscriptionEntity);

            subscribingCustomer.setIsSubscribed(true);
            subscribingCustomer.setIsRenewal(true);
            subscribingCustomer.setExpirationTime(subscriptionEntity.getExpirationTime());
            customerRegistrationRepository.save(subscribingCustomer);

            DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
            String ldtString = FOMATTER.format(subscriptionEntity.getExpirationTime());
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear "
                            + subscribingCustomer.getFirstName() +
                            ", thank you for subscribing to the Teleeza Freemium Package." +
                            " Your subscription starts today and will expire on " + ldtString,
                    phone
            );

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Teleeza Wallet");
            notificationRequest.setMessage("Dear  " + subscribingCustomer.getFirstName() +
                    ", thank you for subscribing to the Teleeza Freemium Package." +
                    " Your subscription starts today and will expire on " + ldtString);
            notificationRequest.setToken(subscribingCustomer.getFcmToken());
            notificationRequest.setTopic("Send Money");
            logger.info("===== Notification Request Body ");
            logger.info("Recipient FCM TOKEN  {}", notificationRequest.getToken());
            logger.info("Notification Request Body  {}", notificationRequest);
            pushNotificationService.sendPushNotificationToToken(notificationRequest);

            //if a user subscribes with a referral code  credit the referring user Ksh 50 as commission and credit the
            // subscribing user Ksh 15 as a 6% subscription discount
            if (subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() != null) {
                // find the referring customer by the referral code(referredByCode) and get their account number
                // send the referring customer Ksh 50 as referral commission and the subscribing customer Ksh 250 as discount

                customerRegistrationRepository.save(subscribingCustomer);

                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
//                freemiumSubscriptionService.sendSubscriptionDiscount(subscribingCustomer.getCustomerAccountNumber(), 15);
//                freemiumSubscriptionService.sendCashback(subscribingCustomer.getCustomerAccountNumber(), 250);
                if (referringCustomer != null) {
                  if(referringCustomer.getMobileUserType().equals("Normal User")){
                      freemiumSubscriptionService.sendReferralCommission(subscribingCustomer.getCustomerAccountNumber(),50);
                  }
                }
            }

            // if a user subscribes without a referral code , credit them a 6 % discount
            if (subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() == null) {
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // on subsequent subscription with referredBy code credit the referrer Ksh 15 residual commission
            if (!subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() != null) {
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // on subsequent subscription without referredBy code update the subscribing user's subscription status to true
            if (!subscribingCustomer.getIsInitialSubscription() && subscribingCustomer.getReferredByCode() == null) {
                customerRegistrationRepository.updateUsersSubscriptionStatus(subscribingCustomer.getCustomerAccountNumber());
            }

            // Save transaction record to Kokotoa
            freemiumSubscriptionService.updateKokotoa(
                    "Teleeza Freemium Subscription",
                    String.valueOf(amountSub),
                    phone, subscriptionAsyncRequest.getSenderAccountNumber(),
                    "Freemium Subscription"
            );

            melioraSubscriptionService.sendSubscriptionStatus(subscribingCustomer.getMobileNumber(), "MONTH", "12");
        }
        return CompletableFuture.completedFuture(acknowledgeResponse);
    }


    @GetMapping("/subscription-status")
    public Map<String, Boolean> getReferrals(String mobileNumber) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("isSubscribed", customerRegistrationRepository.getUsersSubscriptionStatus(mobileNumber)); // pass user's phone number
        return response;
    }

    @PostMapping(path = "/subscription-extension-validation")
//    @ApiIgnore
    @Async
    public CompletableFuture<SubscriptionAsyncRequest> validateSubscriptionExtension(
            @RequestBody SubscriptionAsyncRequest subscriptionAsyncRequest
    ) {

        // get beneficiary details from customer table
        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());

        // get referring customer details
        CustomerEntity referringCustomer = customerRegistrationRepository.findUserByReferralCode(customerEntity.getReferredByCode());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        String subAmount = subscriptionAsyncRequest.getTransactionAmount();
        BigInteger amountSub = new BigDecimal(subAmount).toBigInteger();

        //update record to kokotoa
        Map<String, String> map = new HashMap<>();
        map.put("name", "Freemium Subscription");
        map.put("description",
                "Pay Ksh " + subscriptionAsyncRequest.getTransactionAmount() +
                        " to "
                        + subscriptionAsyncRequest.getRecipientAccountNumber()
                        + "bill number");
        map.put("amount", String.valueOf(amountSub));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("beneficiaryAccount", subscriptionAsyncRequest.getSenderAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        //save in transactions
        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(subscriptionAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(subscriptionAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(subscriptionAsyncRequest.getResultCode());
        transactions.setResultDesc(subscriptionAsyncRequest.getResultDesc());
        transactions.setMerchantCode(subscriptionAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(subscriptionAsyncRequest.getTransactionAmount());
        transactions.setMerchantAccountBalance(subscriptionAsyncRequest.getMerchantAccountBalance());
        transactions.setMerchantTransactionReference(subscriptionAsyncRequest.getMerchantTransactionReference());
        transactions.setTransactionDate(subscriptionAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(subscriptionAsyncRequest.getRecipientAccountNumber());
        transactions.setDestinationChannel(subscriptionAsyncRequest.getDestinationChannel());
        transactions.setSourceChannel(subscriptionAsyncRequest.getSourceChannel());
        transactions.setSasaPayTransactionId(subscriptionAsyncRequest.getSasaPayTransactionID());
        transactions.setRecipientName(subscriptionAsyncRequest.getRecipientName());
        transactions.setReason("Freemium Subscription");
        transactions.setBeneficiaryAccNumber(subscriptionAsyncRequest.getSenderAccountNumber());
        transactions.setSenderAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
        transactions.setIsTransactionType(false);
        transactionsRepository.save(transactions);

        if (subscriptionAsyncRequest.getTransactionAmount().equals("250.00")) {
            SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
            subscriptionEntity.setBeneficiaryAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
            subscriptionEntity.setStartTime(LocalDateTime.now());
            subscriptionEntity.setExpirationTime(LocalDateTime.now().plusDays(30));
            subscriptionEntity.setAmount(subscriptionAsyncRequest.getTransactionAmount());
            subscriptionEntity.setPlanName("Monthly");
            subscriptionEntity.setMerchantReference(subscriptionAsyncRequest.getMerchantRequestID());
            subscriptionEntity.setIsSubscriptionSatus(true);
            subscriptionRepository.save(subscriptionEntity);
//            customerEntity.setIsSubscribed(true);

            customerEntity.setExpirationTime(customerEntity.getExpirationTime().plusDays(30));
            customerRegistrationRepository.save(customerEntity);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
            String ldtString = dateTimeFormatter.format(customerEntity.getExpirationTime());
//            // Send SMS Notification
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear " + customerEntity.getFirstName() + " thank you for renewing your subscription to Teleeza freemium package. " +
                            "Your subscription has been extended to " + ldtString,
                    phone
            );

            // notification
            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Teleeza Wallet");
            notificationRequest.setMessage("Dear  " + customerEntity.getFirstName() +
                    ", thank you for renewing your subscription to Teleeza freemium package." +
                    "Your subscription has been extended to " + ldtString);
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");
            pushNotificationService.sendPushNotificationToToken(notificationRequest);

            if (referringCustomer != null) {
                freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 50);
            }
            melioraSubscriptionService.sendSubscriptionStatus(customerEntity.getMobileNumber(), "MONTH", "1");
        }

        if (subscriptionAsyncRequest.getTransactionAmount().equals("750.00")) {

            SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
            subscriptionEntity.setBeneficiaryAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
            subscriptionEntity.setStartTime(LocalDateTime.now());
            subscriptionEntity.setExpirationTime(LocalDateTime.now().plusDays(90));
            subscriptionEntity.setAmount(subscriptionAsyncRequest.getTransactionAmount());
            subscriptionEntity.setPlanName("Monthly");
            subscriptionEntity.setMerchantReference(subscriptionAsyncRequest.getMerchantRequestID());
            subscriptionEntity.setIsSubscriptionSatus(true);
            subscriptionRepository.save(subscriptionEntity);
            customerEntity.setIsSubscribed(true);

            customerEntity.setExpirationTime(customerEntity.getExpirationTime().plusDays(90));
            customerRegistrationRepository.save(customerEntity);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
            String ldtString = dateTimeFormatter.format(customerEntity.getExpirationTime());

            // Send SMS Notification
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear " + customerEntity.getFirstName() + " thank you for renewing your subscription to Teleeza freemium package. " +
                            "Your subscription has been extended to " + ldtString,
                    phone
            );
            if (referringCustomer != null) {
                freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 150);
                freemiumSubscriptionService.sendSubscriptionDiscount(customerEntity.getCustomerAccountNumber(),150);
            }

            melioraSubscriptionService.sendSubscriptionStatus(customerEntity.getMobileNumber(), "MONTH", "3");
        }

        if (subscriptionAsyncRequest.getTransactionAmount().equals("1500.00")) {
            SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
            subscriptionEntity.setBeneficiaryAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
            subscriptionEntity.setStartTime(LocalDateTime.now());
            subscriptionEntity.setExpirationTime(LocalDateTime.now().plusDays(180));
            subscriptionEntity.setAmount(subscriptionAsyncRequest.getTransactionAmount());
            subscriptionEntity.setPlanName("Monthly");
            subscriptionEntity.setMerchantReference(subscriptionAsyncRequest.getMerchantRequestID());
            subscriptionEntity.setIsSubscriptionSatus(true);
            subscriptionRepository.save(subscriptionEntity);
            customerEntity.setIsSubscribed(true);

            customerEntity.setExpirationTime(customerEntity.getExpirationTime().plusDays(60));
            customerRegistrationRepository.save(customerEntity);

            // Send SMS Notification
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' hh:mm a");
            String ldtString = dateTimeFormatter.format(customerEntity.getExpirationTime());

            // Send SMS Notification
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear " + customerEntity.getFirstName() + " thank you for renewing your subscription to Teleeza freemium package. " +
                            "Your subscription has been extended to " + ldtString,
                    phone
            );

            // notification
            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Teleeza Wallet");
            notificationRequest.setMessage("Dear  " + customerEntity.getFirstName() +
                    ", thank you for renewing your subscription to Teleeza freemium package." +
                    "Your subscription has been extended to " + ldtString);
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");
            pushNotificationService.sendPushNotificationToToken(notificationRequest);

            if (referringCustomer != null) {
                freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 350);
                freemiumSubscriptionService.sendSubscriptionDiscount(referringCustomer.getCustomerAccountNumber(), 200);
            }
            melioraSubscriptionService.sendSubscriptionStatus(customerEntity.getMobileNumber(), "MONTH", "6");
        }

        if (subscriptionAsyncRequest.getTransactionAmount().equals("3000.00")) {
            SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
            subscriptionEntity.setBeneficiaryAccountNumber(subscriptionAsyncRequest.getSenderAccountNumber());
            subscriptionEntity.setStartTime(LocalDateTime.now());
            subscriptionEntity.setExpirationTime(LocalDateTime.now().plusDays(365));
            subscriptionEntity.setAmount(subscriptionAsyncRequest.getTransactionAmount());
            subscriptionEntity.setPlanName("");
            subscriptionEntity.setMerchantReference(subscriptionAsyncRequest.getMerchantRequestID());
            subscriptionEntity.setIsSubscriptionSatus(true);
            subscriptionRepository.save(subscriptionEntity);
            customerEntity.setIsSubscribed(true);

            customerEntity.setExpirationTime(customerEntity.getExpirationTime().plusDays(365));
            customerRegistrationRepository.save(customerEntity);

            // Send SMS Notification
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mm a");
            String ldtString = dateTimeFormatter.format(customerEntity.getExpirationTime());

            // Send SMS Notification
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear " + customerEntity.getFirstName() + " thank you for renewing your subscription to Teleeza freemium package. " +
                            "Your subscription has been extended to " + ldtString,
                    phone
            );

            // notification
            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTitle("Teleeza Wallet");
            notificationRequest.setMessage("Dear  " + customerEntity.getFirstName() +
                    ", thank you for renewing your subscription to Teleeza freemium package." +
                    "Your subscription has been extended to " + ldtString);
            notificationRequest.setToken(customerEntity.getFcmToken());
            notificationRequest.setTopic("Send Money");
            pushNotificationService.sendPushNotificationToToken(notificationRequest);

            if (referringCustomer != null) {
                freemiumSubscriptionService.sendReferralCommission(referringCustomer.getCustomerAccountNumber(), 650);
                freemiumSubscriptionService.sendSubscriptionDiscount(customerEntity.getCustomerAccountNumber(), 250);
            }
            melioraSubscriptionService.sendSubscriptionStatus(customerEntity.getMobileNumber(), "MONTH", "12");
        }
        return CompletableFuture.completedFuture(subscriptionAsyncRequest);
    }
}

