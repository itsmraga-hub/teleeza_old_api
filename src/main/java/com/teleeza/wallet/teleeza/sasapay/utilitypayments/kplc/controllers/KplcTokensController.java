package com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TransactionsEntity;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.AllTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.requests.InternalKplcRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.requests.KplcAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.responses.KplcResponse;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.entity.TokensEntity;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.repository.KplcTokenRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.service.KplcApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
//import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/teleeza-wallet")
public class KplcTokensController {
    private final KplcApi kplcApi;
    private final ObjectMapper objectMapper;
    private final KplcTokenRepository repository;
    private final TransactionsRepository transactionsRepository;

    private final AllTransactionsRepository allTransactionsRepository;

    private final OkHttpClient okHttpClient;

    private final KokotoaConfig kokotoaConfig;
    private final CustomerRegistrationRepository customerRegistrationRepository;

    public KplcTokensController(KplcApi kplcApi, ObjectMapper objectMapper,
                                KplcTokenRepository repository,
                                TransactionsRepository transactionsRepository,
                                AllTransactionsRepository allTransactionsRepository,
                                OkHttpClient okHttpClient, KokotoaConfig kokotoaConfig, CustomerRegistrationRepository customerRegistrationRepository) {
        this.kplcApi = kplcApi;
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.transactionsRepository = transactionsRepository;
        this.allTransactionsRepository = allTransactionsRepository;
        this.okHttpClient = okHttpClient;
        this.kokotoaConfig = kokotoaConfig;
        this.customerRegistrationRepository = customerRegistrationRepository;
    }

    @PostMapping("/buy-kplc-tokens")
    public ResponseEntity<KplcResponse> buyKplcTpokens(
            @RequestBody InternalKplcRequest internalKplcRequest
    ) throws JsonProcessingException {
        KplcResponse response = kplcApi.buyKplcTokens(internalKplcRequest);

        String kplcResponse = objectMapper.writeValueAsString(response);
//        log.info("response body {}", response);
        KplcResponse kplcTokenResponse = objectMapper.readValue(kplcResponse, KplcResponse.class);

        LocalDateTime transactionDate  = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTransactionDate = transactionDate.format(formatter);
        log.info("Local date time {}",formattedTransactionDate);

      // save to all transactions table
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setReason("Buy Tokens");
        transactionsEntity.setIsTransactionType(false);
        transactionsEntity.setBeneficiaryAccNumber(internalKplcRequest.getBeneficiaryAccountNumber());
        transactionsEntity.setAccountNumber(internalKplcRequest.getMeterNumber());
        transactionsEntity.setTransactionDate(formattedTransactionDate);

        TokensEntity tokensEntity = new TokensEntity();
        tokensEntity.setBeneficiaryAccountNumber(internalKplcRequest.getBeneficiaryAccountNumber());
        tokensEntity.setAmount(String.valueOf(internalKplcRequest.getAmount()));
        tokensEntity.setMobileNumber(internalKplcRequest.getMobileNumber());
        tokensEntity.setMeterNumber(internalKplcRequest.getMeterNumber());
        tokensEntity.setMessage(kplcTokenResponse.getMessage());
        tokensEntity.setStatusCode(kplcTokenResponse.getStatusCode());
        repository.save(tokensEntity);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/kplc-validation")
//    @ApiIgnore
    public ResponseEntity<KplcAsyncRequest> validateKplc(
            @RequestBody KplcAsyncRequest kplcAsyncRequest
    ){
        Transactions transactionsEntity = new Transactions();
        transactionsEntity.setMerchantRequestId(kplcAsyncRequest.getServiceCode()); // add service code in place of merchant requets id
        transactionsEntity.setCheckoutRequestId("");
        transactionsEntity.setResultCode(0);
        transactionsEntity.setResultDesc(kplcAsyncRequest.getMessage());
        transactionsEntity.setTransactionAmount(String.valueOf(kplcAsyncRequest.getAmount()));
        transactionsEntity.setTransactionDate(kplcAsyncRequest.getTransTime());
        transactionsEntity.setBillRefNumber(" ");
        transactionsEntity.setMerchantCode("669994");
        transactionsEntity.setRecipientAccountNumber(kplcAsyncRequest.getAccountNumber());
        transactionsEntity.setThirdPartyId("");
        transactionsEntity.setBeneficiaryAccNumber(kplcAsyncRequest.getSenderAccountNumber());
        transactionsEntity.setSenderAccountNumber(kplcAsyncRequest.getSenderAccountNumber());
        transactionsEntity.setIsTransactionType(false);
        transactionsEntity.setSourceChannel("");
        transactionsEntity.setDestinationChannel("");
        transactionsEntity.setRecipientName("");
        transactionsEntity.setReason("Prepaid: KPLC");
        transactionsEntity.setServiceCode(kplcAsyncRequest.getServiceCode());
        transactionsEntity.setVoucherType(kplcAsyncRequest.getVoucherType());
        transactionsEntity.setPin("");
        transactionsEntity.setUnits("");
        transactionsRepository.save(transactionsEntity);

        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(kplcAsyncRequest.getSenderAccountNumber());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);
        log.info("Customer Acc Number {}", customerEntity.getMobileNumber());
        log.info("Phone {}", phone);
        log.info("Customer Name {}", customerEntity.getDisplayName());

        // update Kokotoa
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", "Purchase KPLC Tokens");
        map.put("description", "Buy KPLC Tokens");//+internalCustomerToCustomerRequest.getRecipientBeneficiaryAccountNumber()
        map.put("amount", String.valueOf(kplcAsyncRequest.getAmount()));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("beneficiaryAccount", kplcAsyncRequest.getSenderAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(kplcAsyncRequest);
    }
}
