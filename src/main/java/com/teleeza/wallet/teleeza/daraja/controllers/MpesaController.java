package com.teleeza.wallet.teleeza.daraja.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.repository.BillPaymentRepository;
import com.teleeza.wallet.teleeza.bima.service.impl.BimaServiceImpl;
import com.teleeza.wallet.teleeza.common.AcknowledgeResponse;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.daraja.account_balance.dtos.response.CheckAccountBalanceResponse;
import com.teleeza.wallet.teleeza.daraja.auth.AccessTokenResponse;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.requests.InternalB2CTransactionRequest;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses.B2CTransactionAsyncResponse;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses.B2CTransactionResponse;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.requests.SimulateTransactionRequest;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.responses.RegisterUrlResponse;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.responses.SimulateTransactionResponse;
import com.teleeza.wallet.teleeza.daraja.entity.MpesaTransactions;
import com.teleeza.wallet.teleeza.daraja.repository.MpesaTransactionsRepository;
import com.teleeza.wallet.teleeza.daraja.repository.SubscriptionCommissionsRepository;
import com.teleeza.wallet.teleeza.daraja.service.*;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushAsyncResponse;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushResponse;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.requests.InternalStkPushRequest;
import com.teleeza.wallet.teleeza.notification.service.PushNotificationService;
import com.teleeza.wallet.teleeza.subscription.controller.SubscriptionController;
import com.teleeza.wallet.teleeza.subscription.repository.OrganisationRepository;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionRepository;
import com.teleeza.wallet.teleeza.subscription.service.MelioraSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/mobile-money")
@Slf4j
public class MpesaController {
    private final DarajaApi darajaApi;
    private final AcknowledgeResponse acknowledgeResponse;
    private final ObjectMapper objectMapper;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final SubscriptionRepository subscriptionRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MpesaTransactionsRepository mpesaTransactionsRepository;

    @Autowired
    private AdvantaSmsApiImpl advantaSmsApiImpl;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private MelioraSubscriptionService melioraSubscriptionService;
    @Autowired
    private DarajaApiImpl darajaApiImpl;
    @Autowired
    private BillPaymentRepository billPaymentRepository;
    @Autowired
    private SubscriptionCommissionsRepository subscriptionCommissionsRepository;
    private final BimaServiceImpl bimaService;

    private final SmsNotificationObserver smsObserver;
    private final UserDatabaseObserver dbObserver;
    private final BimaPolicyObserver bimaPolicyObserver;
    private final PaymentCallBackSubject callbackSubject;


    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    public MpesaController(DarajaApi darajaApi, AcknowledgeResponse acknowledgeResponse,
                           ObjectMapper objectMapper,
                           CustomerRegistrationRepository customerRegistrationRepository,
                           SubscriptionRepository subscriptionRepository,
                           BimaServiceImpl bimaService,
                           SmsNotificationObserver smsObserver,
                           UserDatabaseObserver dbObserver,
                           BimaPolicyObserver bimaPolicyObserver,

                           PaymentCallBackSubject callbackSubject) {
        this.darajaApi = darajaApi;
        this.acknowledgeResponse = acknowledgeResponse;
        this.objectMapper = objectMapper;
        this.customerRegistrationRepository = customerRegistrationRepository;

        this.subscriptionRepository = subscriptionRepository;
        this.bimaService = bimaService;
        this.smsObserver = smsObserver;
        this.dbObserver = dbObserver;
        this.bimaPolicyObserver = bimaPolicyObserver;
        this.callbackSubject = callbackSubject;

        // Register the observers with the subject
        this.callbackSubject.registerObserver(smsObserver);
        this.callbackSubject.registerObserver(dbObserver);
        this.callbackSubject.registerObserver(bimaPolicyObserver);
    }

    @GetMapping("/token")
    public ResponseEntity<AccessTokenResponse> getDarajaAccessToken() {
        return ResponseEntity.ok(darajaApi.getAccessToken());
    }

    @GetMapping(path = "/register-url", produces = "application/json")
    public ResponseEntity<RegisterUrlResponse> registerUrl() {
        return ResponseEntity.ok(darajaApi.registerUrl());
    }

//    @PostMapping(path = "/validation", produces = "application/json")
//    public ResponseEntity<AcknowledgeResponse> mpesaValidation(@RequestBody MpesaValidationResponse mpesaValidationResponse) {
//        log.info("==========================================");
//        log.info("Validation URL ");
//        log.info("Validated Response : {}", mpesaValidationResponse);
//        log.info("==========================================");
//
//        CustomerEntity subscribingCustomer = customerRegistrationRepository
//                .findByCustomerAccountNumber(mpesaValidationResponse.getBillRefNumber());
//
//        String customerPhone = subscribingCustomer.getMobileNumber();
//        String phone = "" + customerPhone.substring(1);
//
//        String subAmount = mpesaValidationResponse.getTransAmount();
//        BigInteger amountSub = new BigDecimal(subAmount).toBigInteger();
//
//        CustomerEntity referringCustomer = customerRegistrationRepository.findUserByReferralCode(subscribingCustomer.getReferredByCode());
//        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
//
//
//        return ResponseEntity.ok(acknowledgeResponse);
//    }

//    @PostMapping(path = "/confirmation", produces = "application/json")
//    public ResponseEntity<AcknowledgeResponse> mpesaConfirmation(@RequestBody MpesaValidationResponse mpesaValidationResponse) {
//        log.info("==========================================");
//        log.info("Confirmation URL");
//        log.info("==========================================");
//
//        return ResponseEntity.ok(acknowledgeResponse);
//    }

    @PostMapping(path = "/simulate-c2b", produces = "application/json")
    public ResponseEntity<SimulateTransactionResponse> simulateB2CTransaction(@RequestBody SimulateTransactionRequest simulateTransactionRequest) {
        return ResponseEntity.ok(darajaApi.simulateC2BTransaction(simulateTransactionRequest));
    }

    @PostMapping(path = "/b2c-transaction", produces = "application/json")
    public ResponseEntity<B2CTransactionResponse> performB2CTransaction(@RequestBody InternalB2CTransactionRequest internalB2CTransactionRequest) {
        return ResponseEntity.ok(darajaApi.performB2CTransaction(internalB2CTransactionRequest));
    }

    @PostMapping(path = "/b2c-queue-timeout", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> queueTimeout(@RequestBody Object object) {
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/stk-transaction-request", produces = "application/json")
    public ResponseEntity<StkPushResponse> performStkPushTransaction(@RequestBody InternalStkPushRequest internalStkPushRequest) {
        return ResponseEntity.ok(darajaApi.performStkPushTransaction(internalStkPushRequest));
    }

    @PostMapping(path = "/stk-transaction-callback", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> acknowledgeStkPushResponse(@RequestBody StkPushAsyncResponse stkPushAsyncResponse) throws JsonProcessingException {
        log.info("======= STK Push Async Response =====");
        log.info(objectMapper.writeValueAsString(stkPushAsyncResponse));
        callbackSubject.handleCallback(stkPushAsyncResponse);
        return ResponseEntity.ok(acknowledgeResponse);
    }

//    @PostMapping(path = "/create-bima-policy")
//    public ResponseEntity<AcknowledgeResponse> createPolicy(@RequestBody StkPushAsyncResponse stkPushAsyncResponse) throws JsonProcessingException {
//        log.info("===== CREATE POLICY ====");
////        bimaService.creatPolicy(stkPushAsyncResponse);
//        return ResponseEntity.ok(acknowledgeResponse);
//    }

    @PostMapping(path = "/transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> b2cTransactionAsyncResults(@RequestBody B2CTransactionAsyncResponse
                                                                                  b2CTransactionAsyncResponse)
            throws JsonProcessingException {
        log.info("============ Transaction Result =============");
        log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));

        MpesaTransactions mpesaTransactions = mpesaTransactionsRepository
                .findByConversationIDOrOriginatorConversationID(
                        b2CTransactionAsyncResponse.getResult().getConversationID(),
                        b2CTransactionAsyncResponse.getResult().getOriginatorConversationID()
                );

        int responseCode = b2CTransactionAsyncResponse.getResult().getResultCode();
        if (responseCode == 0) {
            mpesaTransactions.setResultDesc(b2CTransactionAsyncResponse.getResult().getResultDesc());
            mpesaTransactions.setResultCode(b2CTransactionAsyncResponse.getResult().getResultCode());
            mpesaTransactions.setMpesaReceiptNumber(b2CTransactionAsyncResponse.getResult().getTransactionID());
            mpesaTransactions.setUtilityBalance(b2CTransactionAsyncResponse.getResult().getResultParameters().getResultParameter().get(4).getValue());
            mpesaTransactions.setTransactionDate(b2CTransactionAsyncResponse.getResult().getResultParameters().getResultParameter().get(3).getValue());
            mpesaTransactionsRepository.save(mpesaTransactions);

        } else {
            mpesaTransactions.setResultDesc(b2CTransactionAsyncResponse.getResult().getResultDesc());
            mpesaTransactions.setResultCode(b2CTransactionAsyncResponse.getResult().getResultCode());
            mpesaTransactionsRepository.save(mpesaTransactions);
            log.info("B2C FAILED");
        }
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @GetMapping(path = "/check-account-balance", produces = "application/json")
    public ResponseEntity<CheckAccountBalanceResponse> checkAccountBalance() {
        return ResponseEntity.ok(darajaApi.checkAccountBalance());
    }
}


