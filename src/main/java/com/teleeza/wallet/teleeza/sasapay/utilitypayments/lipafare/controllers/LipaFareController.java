package com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.requests.InternalLipaFareRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.requests.LipaFareAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.responses.LipaFareResponse;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.entity.LipaFareEntity;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.repository.LipaFareRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.service.LipaFareApi;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TransactionsEntity;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.AllTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/teleeza-wallet")
public class LipaFareController {
    private final LipaFareApi lipaFareApi;
    private final ObjectMapper objectMapper;
    private final AllTransactionsRepository allTransactionsRepository;
    private final TransactionsRepository transactionsRepository;
    private final KokotoaConfig kokotoaConfig;
    @Autowired
    private LipaFareRepository lipaFareRepository;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;

    public LipaFareController(LipaFareApi lipaFareApi,
                              ObjectMapper objectMapper,
                              AllTransactionsRepository allTransactionsRepository,
                              TransactionsRepository transactionsRepository, KokotoaConfig kokotoaConfig) {
        this.lipaFareApi = lipaFareApi;
        this.objectMapper = objectMapper;
        this.allTransactionsRepository = allTransactionsRepository;
        this.transactionsRepository = transactionsRepository;

        this.kokotoaConfig = kokotoaConfig;
    }

    @PostMapping("/lip-fare")
    public ResponseEntity<LipaFareResponse> lipaFare(
            @RequestBody InternalLipaFareRequest internalLipaFareRequest
    ) throws JsonProcessingException {
        LipaFareResponse lipaFare = lipaFareApi.lipaFare(internalLipaFareRequest);
        String response = objectMapper.writeValueAsString(lipaFare);
        LipaFareResponse lipaFareResponse = objectMapper.readValue(response, LipaFareResponse.class);

        // update record on all transactions table
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setBeneficiaryAccNumber(internalLipaFareRequest.getBeneficiaryAccountNumber());
        transactionsEntity.setAccountNumber(String.valueOf(internalLipaFareRequest.getMatatuBillNumber()));
        transactionsEntity.setAmount((double) internalLipaFareRequest.getAmount());
        transactionsEntity.setMerchantCode("669994");
        transactionsEntity.setReason("Lipa fare to " + internalLipaFareRequest.getMatatuBillNumber());
        transactionsEntity.setMerchantTransactionRef(lipaFareResponse.getMerchantReference());
        transactionsEntity.setTransactionReference(lipaFareResponse.getReferenceNumber());
        transactionsEntity.setResultCode(lipaFareResponse.getStatusCode());
        transactionsEntity.setResultDesc(lipaFareResponse.getMessage());
        allTransactionsRepository.save(transactionsEntity);

        LipaFareEntity lipaFareEntity = new LipaFareEntity();
        lipaFareEntity.setMatatuBillNumber(String.valueOf(internalLipaFareRequest.getMatatuBillNumber()));
        lipaFareEntity.setAmount(String.valueOf(internalLipaFareRequest.getAmount()));
        lipaFareEntity.setMerchantCode("669994");
        lipaFareEntity.setBeneficiaryAccountNumber(internalLipaFareRequest.getBeneficiaryAccountNumber());
        lipaFareEntity.setReason("Lipa Fare to " + internalLipaFareRequest.getMatatuBillNumber());
        lipaFareEntity.setStatusCode(lipaFareResponse.getStatusCode());
        lipaFareEntity.setMessage(lipaFareResponse.getMessage());
        lipaFareEntity.setTransactionReference(lipaFareResponse.getMerchantReference());
        lipaFareEntity.setReferenceNumber(lipaFareEntity.getReferenceNumber());
        lipaFareRepository.save(lipaFareEntity);
        return ResponseEntity.ok(lipaFare);
    }

    @PostMapping("/lip-fare-validation")
//    @ApiIgnore
    public ResponseEntity<LipaFareAsyncRequest> validateLipaFare(
            @RequestBody LipaFareAsyncRequest lipaFareAsyncRequest
    ) {

        CustomerEntity entity =
                customerRegistrationRepository.findByCustomerAccountNumber(lipaFareAsyncRequest.getSenderAccountNumber());

        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(lipaFareAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(lipaFareAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(lipaFareAsyncRequest.getResultCode());
        transactions.setResultDesc(lipaFareAsyncRequest.getResultDesc());
        transactions.setMerchantCode(lipaFareAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(lipaFareAsyncRequest.getTransactionAmount());
        transactions.setMerchantAccountBalance(lipaFareAsyncRequest.getMerchantAccountBalance());
        transactions.setMerchantTransactionReference(lipaFareAsyncRequest.getMerchantTransactionReference());
        transactions.setTransactionDate(lipaFareAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(lipaFareAsyncRequest.getRecipientAccountNumber());
        transactions.setDestinationChannel(lipaFareAsyncRequest.getDestinationChannel());
        transactions.setSourceChannel(lipaFareAsyncRequest.getSourceChannel());
        transactions.setSasaPayTransactionId(lipaFareAsyncRequest.getSasaPayTransactionID());
        transactions.setRecipientName(lipaFareAsyncRequest.getRecipientName());
        transactions.setSenderName(entity.getDisplayName());
        transactions.setReason("Lipa Fare");
        transactions.setBeneficiaryAccNumber(lipaFareAsyncRequest.getSenderAccountNumber());
        transactions.setSenderAccountNumber(lipaFareAsyncRequest.getSenderAccountNumber());
        transactions.setIsTransactionType(false);
        transactionsRepository.save(transactions);

        String amount = lipaFareAsyncRequest.getTransactionAmount();
        BigInteger intAmount = new BigDecimal(amount).toBigInteger();
        //update record to kokotoa
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", "Lipa Fare");
        map.put("description",
                "Pay Ksh " + lipaFareAsyncRequest.getTransactionAmount() +
                        " fare to "
                        + lipaFareAsyncRequest.getRecipientAccountNumber()
                        + "till number");
        map.put("amount", String.valueOf(intAmount));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", "");
        map.put("beneficiaryAccount", lipaFareAsyncRequest.getSenderAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(lipaFareAsyncRequest);
    }
}
