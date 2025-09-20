package com.teleeza.wallet.teleeza.sasapay.load_wallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.common.AcknowledgeResponse;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.CustomerResponse;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests.InternalLoadCustomerWalletRequest;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests.TopUpAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.responses.LoadCustomerWalletResponse;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.entities.LoadWalletEntity;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.repository.LoadWalletRepository;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.AllTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/v1/teleeza-wallet")
@Slf4j
public class LoadCustomerWalletController {
    private final SasaPayApi sasapayApi;
    private final LoadWalletRepository repository;
    private final AllTransactionsRepository allTransactionsRepository;
    private final TransactionsRepository transactionsRepository;
    private final KokotoaConfig kokotoaConfig;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final LoadWalletRepository loadWalletRepository;
    private final ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;

    private final AcknowledgeResponse acknowledgeResponse;
    private static volatile AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    private static volatile ConcurrentHashMap<String, DeferredResult<String>> DEFERRED_RESULT = new ConcurrentHashMap<>(20000);


    public LoadCustomerWalletController(SasaPayApi sasapayApi, LoadWalletRepository repository,
                                        AllTransactionsRepository allTransactionsRepository,
                                        TransactionsRepository transactionsRepository, KokotoaConfig kokotoaConfig, CustomerRegistrationRepository customerRegistrationRepository, LoadWalletRepository loadWalletRepository, ObjectMapper objectMapper,
                                        AcknowledgeResponse acknowledgeResponse) {
        this.sasapayApi = sasapayApi;
        this.repository = repository;
        this.allTransactionsRepository = allTransactionsRepository;
        this.transactionsRepository = transactionsRepository;
        this.kokotoaConfig = kokotoaConfig;
        this.customerRegistrationRepository = customerRegistrationRepository;
        this.loadWalletRepository = loadWalletRepository;
        this.objectMapper = objectMapper;
        this.acknowledgeResponse = acknowledgeResponse;
    }

    @PostMapping(value = "/load-wallet", produces = "application/json")
    @Transactional
    public ResponseEntity<LoadCustomerWalletResponse> loadCustomerWallet(
            @RequestBody InternalLoadCustomerWalletRequest internalLoadCustomerWalletRequest
    ) throws JsonProcessingException {
        LoadCustomerWalletResponse loadWalletResponse =
                sasapayApi.merchantLoadCustomerWallet(internalLoadCustomerWalletRequest);
        String response = objectMapper.writeValueAsString(loadWalletResponse);

        LoadCustomerWalletResponse walletResponse = objectMapper.readValue(response, LoadCustomerWalletResponse.class);

        return ResponseEntity.ok(loadWalletResponse);
    }

    @PostMapping("/validation")
    public ResponseEntity<TopUpAsyncRequest>
    validateTopup(@RequestBody TopUpAsyncRequest topUpAsyncRequest) {

        LoadWalletEntity loadWallet = loadWalletRepository.findByMerchantReference(topUpAsyncRequest.getMerchantRequestID());
        log.info("Load Wallet Attempt : {}",loadWallet);
        log.info("Load Wallet Reason : {}",loadWallet.getReason());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(sasapayApi.getAccessToken().getAccessToken());

        String url = String.format("%s?CustomerAccountNumber=%s", "https://api.sasapay.app//api/v1/waas/customers/detail/", loadWallet.getBeneficiaryAccountNumber());

//        String url = "https://sandbox.sasapay.app//api/v1/waas/customers/detail/?CustomerAccountNumber=Cus";

        // build the request
        HttpEntity request = new HttpEntity(httpHeaders);

        // make an HTTP GET request with headers
        ResponseEntity<CustomerResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                CustomerResponse.class

        );

        // check response
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful.");
            System.out.println(response.getBody());

            Map<String, Object> map = new HashMap<String, Object>();
            log.info("Balance {}", Objects.requireNonNull(response.getBody()).getResult().getCustomerBalance());
            map.put("balance", response.getBody().getResult().getCustomerBalance());
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }




        CustomerEntity customer = customerRegistrationRepository.findByCustomerAccountNumber(loadWallet.getBeneficiaryAccountNumber());
        String phone = ""+customer.getMobileNumber().substring(1);

        Transactions transactionsEntity = new Transactions();


        String topUpAmnt = topUpAsyncRequest.getTransAmount();
        BigInteger amountToppedUp = new BigDecimal(topUpAmnt).toBigInteger();

        transactionsEntity.setMerchantRequestId(topUpAsyncRequest.getMerchantRequestID());
        transactionsEntity.setCheckoutRequestId(topUpAsyncRequest.getCheckoutRequestID());
        transactionsEntity.setResultCode(topUpAsyncRequest.getResultCode());
        transactionsEntity.setResultDesc(topUpAsyncRequest.getResultDesc());
        transactionsEntity.setTransactionAmount(topUpAsyncRequest.getTransAmount());
        transactionsEntity.setTransactionDate(topUpAsyncRequest.getTransactionDate());
        transactionsEntity.setBillRefNumber(topUpAsyncRequest.getBillRefNumber());
        transactionsEntity.setMerchantCode("669994");
        transactionsEntity.setRecipientAccountNumber(transactionsEntity.getBeneficiaryAccNumber());
        transactionsEntity.setThirdPartyId(topUpAsyncRequest.getThirdPartyTransID());
        transactionsEntity.setIsTransactionType(true);
        transactionsEntity.setSenderAccountNumber(topUpAsyncRequest.getCustomerMobile());
        transactionsEntity.setDestinationChannel("0");
        transactionsEntity.setReason(loadWallet.getReason());
        transactionsEntity.setSourceChannel(loadWallet.getSourceChannel());
        transactionsEntity.setRecipientName(customer.getDisplayName());
        transactionsEntity.setBeneficiaryAccNumber(customer.getCustomerAccountNumber());
        transactionsEntity.setResultDesc("Transaction processed successfully.");
        transactionsEntity.setResultCode(0);
        transactionsEntity.setCustomerBalance(response.getBody().getResult().getCustomerBalance());
        transactionsRepository.save(transactionsEntity);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", "Top Up Wallet");
        map.put("description", "Load Wallet Ksh " + topUpAsyncRequest.getTransAmount() + " From Mobile Money");
        map.put("amount", String.valueOf(amountToppedUp));
        map.put("category", "Income");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("transactionFee","0");
        map.put("beneficiaryAccount", transactionsEntity.getBeneficiaryAccNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful");
            System.out.println("Request Successful");
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }
        return new ResponseEntity<>(topUpAsyncRequest, HttpStatus.OK);
    }

}