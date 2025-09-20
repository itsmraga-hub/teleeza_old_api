package com.teleeza.wallet.teleeza.sasapay.cashout.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.cashout.dtos.requests.CashoutAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.cashout.dtos.requests.InternalCashoutRequest;
import com.teleeza.wallet.teleeza.sasapay.cashout.dtos.responses.CashoutResponse;
import com.teleeza.wallet.teleeza.sasapay.cashout.entity.CashoutEntity;
import com.teleeza.wallet.teleeza.sasapay.cashout.repository.CashoutTransactionsRepository;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
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

@RestController
@RequestMapping("v1/teleeza-wallet")
@Slf4j
public class CashoutController {
    private final SasaPayApi sasaPayApi;
    private final ObjectMapper objectMapper;
    private final CashoutTransactionsRepository repository;
    private final KokotoaConfig kokotoaConfig;
    private final TransactionsRepository transactionsRepository;
    @Autowired private CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired private RestTemplate restTemplate;

    public CashoutController(SasaPayApi sasaPayApi, ObjectMapper objectMapper, CashoutTransactionsRepository repository, KokotoaConfig kokotoaConfig, TransactionsRepository transactionsRepository) {
        this.sasaPayApi = sasaPayApi;
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.kokotoaConfig = kokotoaConfig;
        this.transactionsRepository = transactionsRepository;
    }

    @PostMapping("/agent-withdrawal")
    public ResponseEntity<CashoutResponse> withdrawFromAgent(
            @RequestBody InternalCashoutRequest internalCashoutRequest) throws JsonProcessingException {
        //
        CashoutResponse cashout = sasaPayApi.withDrawFromAgent(internalCashoutRequest);
        String response = objectMapper.writeValueAsString(cashout);
        log.info("response body {}", response);
        CashoutResponse cashoutResponse = objectMapper.readValue(response, CashoutResponse.class);
        log.info("deserialized body {}", cashoutResponse);


        CashoutEntity entity = new CashoutEntity();
        entity.setBeneficiaryAccountNumber(internalCashoutRequest.getBeneficiaryAccountNumber());
        entity.setSasaPayAgentNumber(String.valueOf(internalCashoutRequest.getSasaPayAgentNumber()));
        entity.setAmount(String.valueOf(internalCashoutRequest.getAmount()));
        entity.setMerchantCode("669994");
        entity.setReason("Agent Withdrawal");
        entity.setTransactionReference(cashoutResponse.getMerchantReference());
        entity.setMerchantReference(cashoutResponse.getMerchantReference());
        entity.setStatusCode(cashoutResponse.getStatusCode());
        repository.save(entity);

        return ResponseEntity.ok(cashout);
    }

    @PostMapping("/cashout-validation")
    public ResponseEntity<CashoutAsyncRequest> validateCashoutTransactions(@RequestBody CashoutAsyncRequest cashoutAsyncRequest){

//        String destinationChannel = mobileMoneyTransferAsyncRequest.getDestinationChannel();
        double transactedAmount = Double.parseDouble("50.00");
        int amountTransacted = (int) transactedAmount;
        String transactionFee = amountTransacted <= 50 ? "0" :
                amountTransacted >= 51 && amountTransacted <= 100 ? "17" :
                        amountTransacted >= 101 && amountTransacted <= 500 ? "18" :
                                amountTransacted >= 501 && amountTransacted <= 1000 ? "22" :
                                        amountTransacted >= 1001 && amountTransacted <= 3000 ? "30" :
                                                amountTransacted >= 3001 && amountTransacted <= 10000 ? "35" :
                                                        amountTransacted >= 10001 && amountTransacted <= 20000 ? "40" :
                                                                amountTransacted >= 20001 && amountTransacted <= 70001 ? "50" :
                                                                        amountTransacted >= 70001 && amountTransacted <= 150000 ? "50" : "";

        // get beneficiary details from customer table
        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(cashoutAsyncRequest.getSenderAccountNumber());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

//        Transactions transactions = new Transactions();
//        transactions.setMerchantRequestId(billPaymentAsyncRequest.getMerchantRequestID());
//        transactions.setCheckoutRequestId(billPaymentAsyncRequest.getCheckoutRequestID());
//        transactions.setResultCode(billPaymentAsyncRequest.getResultCode());
//        transactions.setResultDesc(billPaymentAsyncRequest.getResultDesc());
//        transactions.setMerchantCode(billPaymentAsyncRequest.getMerchantCode());
//        transactions.setTransactionAmount(billPaymentAsyncRequest.getTransactionAmount());
//        transactions.setMerchantAccountBalance(billPaymentAsyncRequest.getMerchantAccountBalance());
//        transactions.setMerchantTransactionReference(billPaymentAsyncRequest.getMerchantTransactionReference());
//        transactions.setTransactionDate(billPaymentAsyncRequest.getTransactionDate());
//        transactions.setRecipientAccountNumber(billPaymentAsyncRequest.getRecipientAccountNumber());
//        transactions.setDestinationChannel(billPaymentAsyncRequest.getDestinationChannel());
//        transactions.setSourceChannel(billPaymentAsyncRequest.getSourceChannel());
//        transactions.setSasaPayTransactionId(billPaymentAsyncRequest.getSasaPayTransactionID());
//        transactions.setRecipientName(billPaymentAsyncRequest.getRecipientName());
//        transactions.setReason("Pay Bill");
//        transactions.setSenderName(customerEntity.getDisplayName());
//        transactions.setRecipientName(billPaymentAsyncRequest.getRecipientName());
//        transactions.setBeneficiaryAccNumber(billPaymentAsyncRequest.getSenderAccountNumber());
//        transactions.setSenderAccountNumber(billPaymentAsyncRequest.getSenderAccountNumber());
//        transactions.setIsTransactionType(false);
//        transactions.setRecipientName(billPaymentAsyncRequest.getRecipientName());
//        transactions.setSenderName(customerEntity.getDisplayName());
//        transactionsRepository.save(transactions);

        Transactions transactions  = new Transactions();
        transactions.setMerchantRequestId(cashoutAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(cashoutAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(cashoutAsyncRequest.getResultCode());
        transactions.setResultDesc(cashoutAsyncRequest.getResultDesc());
        transactions.setMerchantCode(cashoutAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(cashoutAsyncRequest.getTransactionAmount());
        transactions.setMerchantAccountBalance(cashoutAsyncRequest.getMerchantAccountBalance());
        transactions.setMerchantTransactionReference(cashoutAsyncRequest.getMerchantTransactionReference());
        transactions.setTransactionDate(cashoutAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(String.valueOf(cashoutAsyncRequest.getRecipientAccountNumber()));
        transactions.setDestinationChannel(cashoutAsyncRequest.getDestinationChannel());
        transactions.setTransactionFee(transactionFee);
        transactions.setSourceChannel(cashoutAsyncRequest.getSourceChannel());
        transactions.setSasaPayTransactionId(cashoutAsyncRequest.getSasaPayTransactionID());
        transactions.setRecipientName(cashoutAsyncRequest.getRecipientName());
        transactions.setReason("Withdraw");
        transactions.setSenderName(customerEntity.getDisplayName());
        transactions.setIsTransactionType(false);
        transactionsRepository.save(transactions);

        String withDrawAmount = cashoutAsyncRequest.getTransactionAmount();
        BigInteger amountWithdraw = new BigDecimal(withDrawAmount).toBigInteger();

        //update record to kokotoa
        Map<String, String> map = new HashMap<>();
        map.put("name", "Wallet:Cashout");
        map.put("Withdraw",
                " Ksh " + cashoutAsyncRequest.getTransactionAmount() +
                        " from "
                        + cashoutAsyncRequest.getRecipientAccountNumber()
                        + "bill number");
        map.put("amount", String.valueOf(amountWithdraw));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("transactionFee",transactionFee);
        map.put("beneficiaryAccount", cashoutAsyncRequest.getSenderAccountNumber());
        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(cashoutAsyncRequest);
    }
}
