package com.teleeza.wallet.teleeza.sasapay.till_payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests.InternalTillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests.TillPaymentAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.responses.TillPaymentResponse;
import com.teleeza.wallet.teleeza.sasapay.till_payment.repository.TillsTransactionRepository;
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
//import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/teleeza-wallet")
public class LipaTillController {
    private final SasaPayApi sasaPayApi;
    private final ObjectMapper objectMapper;
    private final TillsTransactionRepository repository;
    private final AllTransactionsRepository allTransactionsRepository;
    private final TransactionsRepository transactionsRepository;
    @Autowired
    private TillsTransactionRepository tillsTransactionRepository;


    public LipaTillController(SasaPayApi sasaPayApi, ObjectMapper objectMapper, TillsTransactionRepository repository, AllTransactionsRepository allTransactionsRepository, TransactionsRepository transactionsRepository) {
        this.sasaPayApi = sasaPayApi;
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.allTransactionsRepository = allTransactionsRepository;
        this.transactionsRepository = transactionsRepository;
    }

    @PostMapping("lipa-till")
    public ResponseEntity<TillPaymentResponse> payToTill(
            @RequestBody InternalTillPaymentRequest internalTillPaymentRequest
    ) throws JsonProcessingException {

        TillPaymentResponse payToTills =
                sasaPayApi.payToTills(internalTillPaymentRequest);
        String response = objectMapper.writeValueAsString(payToTills);

        TillPaymentResponse tillPaymentResponse = objectMapper.readValue(response, TillPaymentResponse.class);

        // update recorde on all transactions table
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setBeneficiaryAccNumber(internalTillPaymentRequest.getBeneficiaryAccountNumber());
        transactionsEntity.setRecipientAccountNumber(String.valueOf(internalTillPaymentRequest.getSasaPayBillNumber()));
        transactionsEntity.setAmount((double) internalTillPaymentRequest.getAmount());
        transactionsEntity.setMerchantCode("669994");
        transactionsEntity.setReason("Lipa Till");
        transactionsEntity.setTransactionFee(0);
        transactionsEntity.setTransactionReference(tillPaymentResponse.getReferenceNumber());
        transactionsEntity.setMerchantTransactionRef(tillPaymentResponse.getMerchantReference());
        transactionsEntity.setResultCode(tillPaymentResponse.getStatusCode());
        transactionsEntity.setResultDesc(tillPaymentResponse.getMessage());
        allTransactionsRepository.save(transactionsEntity);


        return ResponseEntity.ok(payToTills);
    }

    @PostMapping("/till-validation")
//    @ApiIgnore
    public ResponseEntity<TillPaymentAsyncRequest> validateTillTransactions(
            @RequestBody TillPaymentAsyncRequest tillPaymentAsyncRequest
    ) {

        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(tillPaymentAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(tillPaymentAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(tillPaymentAsyncRequest.getResultCode());
        transactions.setResultDesc(tillPaymentAsyncRequest.getResultDesc());
        transactions.setMerchantCode(tillPaymentAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(tillPaymentAsyncRequest.getTransactionAmount());
        transactions.setMerchantAccountBalance(tillPaymentAsyncRequest.getMerchantAccountBalance());
        transactions.setMerchantTransactionReference(tillPaymentAsyncRequest.getMerchantTransactionReference());
        transactions.setTransactionDate(tillPaymentAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(tillPaymentAsyncRequest.getRecipientAccountNumber());
        transactions.setDestinationChannel(tillPaymentAsyncRequest.getDestinationChannel());
        transactions.setSourceChannel(tillPaymentAsyncRequest.getSourceChannel());
        transactions.setSasaPayTransactionId(tillPaymentAsyncRequest.getSasaPayTransactionID());
        transactions.setRecipientName(tillPaymentAsyncRequest.getRecipientName());
        transactions.setReason("Lipa Till");
        transactions.setBeneficiaryAccNumber(tillPaymentAsyncRequest.getSenderAccountNumber());
        transactions.setSenderAccountNumber(tillPaymentAsyncRequest.getSenderAccountNumber());
        transactions.setIsTransactionType(false);
        transactionsRepository.save(transactions);

        String tillAmount = tillPaymentAsyncRequest.getTransactionAmount();
        BigInteger amountPaid = new BigDecimal(tillAmount).toBigInteger();

        //update record to kokotoa
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", "Wallet: Lipa Till");
        map.put("description", "Pay Ksh " + tillPaymentAsyncRequest.getTransactionAmount() + " to " + tillPaymentAsyncRequest.getRecipientAccountNumber() + " till number");
        map.put("amount", String.valueOf(amountPaid));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", "");
        map.put("beneficiaryAccount", tillPaymentAsyncRequest.getSenderAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                "https://kokotoa.teleeza.africa/api/callback",
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }
        return ResponseEntity.ok(tillPaymentAsyncRequest);
    }
}
