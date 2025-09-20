package com.teleeza.wallet.teleeza.sasapay.bill_payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.requests.BillPaymentAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.requests.InternalBillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.responses.BillPaymentResponse;
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
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/teleeza-wallet")
public class BillPaymentController {
    private final SasaPayApi sasaPayApi;
    private final ObjectMapper objectMapper;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final TransactionsRepository transactionsRepository;
    private final KokotoaConfig kokotoaConfig;
    @Autowired
    private RestTemplate restTemplate;

    public BillPaymentController(SasaPayApi sasaPayApi, ObjectMapper objectMapper,
                                 CustomerRegistrationRepository customerRegistrationRepository,
                                 TransactionsRepository transactionsRepository, KokotoaConfig kokotoaConfig) {
        this.sasaPayApi = sasaPayApi;
        this.objectMapper = objectMapper;
        this.customerRegistrationRepository = customerRegistrationRepository;
        this.transactionsRepository = transactionsRepository;
        this.kokotoaConfig = kokotoaConfig;
    }

    @PostMapping(path = "/pay-bill", produces = "application/json")
    public ResponseEntity<BillPaymentResponse> payBill(
            @RequestBody InternalBillPaymentRequest internalBillPaymentRequest
    ) throws JsonProcessingException {
        BillPaymentResponse payBill = sasaPayApi.payBills(internalBillPaymentRequest);
        String response = objectMapper.writeValueAsString(payBill);
        BillPaymentResponse billPaymentResponse = objectMapper.readValue(response, BillPaymentResponse.class);
        return ResponseEntity.ok(payBill);
    }

    @PostMapping(path = "/paybills-validation", produces = "application/json")
    public CompletableFuture<BillPaymentAsyncRequest> validatePayBillsTransactions(
            @RequestBody BillPaymentAsyncRequest billPaymentAsyncRequest
    ) {
        // get beneficiary details from customer table
        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(billPaymentAsyncRequest.getSenderAccountNumber());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        double transactedAmount = Double.parseDouble(billPaymentAsyncRequest.getTransactionAmount());
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

        // save response from SasaPay callback as valid transactions
        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(billPaymentAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(billPaymentAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(billPaymentAsyncRequest.getResultCode());
        transactions.setResultDesc(billPaymentAsyncRequest.getResultDesc());
        transactions.setMerchantCode(billPaymentAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(billPaymentAsyncRequest.getTransactionAmount());
        transactions.setMerchantAccountBalance(billPaymentAsyncRequest.getMerchantAccountBalance());
        transactions.setMerchantTransactionReference(billPaymentAsyncRequest.getMerchantTransactionReference());
        transactions.setTransactionDate(billPaymentAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(billPaymentAsyncRequest.getRecipientAccountNumber());
        transactions.setDestinationChannel(billPaymentAsyncRequest.getDestinationChannel());
        transactions.setSourceChannel(billPaymentAsyncRequest.getSourceChannel());
        transactions.setSasaPayTransactionId(billPaymentAsyncRequest.getSasaPayTransactionID());
        transactions.setRecipientName(billPaymentAsyncRequest.getRecipientName());
        transactions.setReason("Pay Bill");
        transactions.setTransactionFee(transactionFee);
        transactions.setSenderName(customerEntity.getDisplayName());
        transactions.setRecipientName(billPaymentAsyncRequest.getRecipientName());
        transactions.setBeneficiaryAccNumber(billPaymentAsyncRequest.getSenderAccountNumber());
        transactions.setSenderAccountNumber(billPaymentAsyncRequest.getSenderAccountNumber());
        transactions.setIsTransactionType(false);
        transactions.setRecipientName(billPaymentAsyncRequest.getRecipientName());
        transactions.setSenderName(customerEntity.getDisplayName());
        transactionsRepository.save(transactions);

        String billAmount = billPaymentAsyncRequest.getTransactionAmount();
        BigInteger amountPaid = new BigDecimal(billAmount).toBigInteger();

        //update record to kokotoa
        Map<String, String> map = new HashMap<>();
        map.put("name", "Wallet:PayBill");
        map.put("description",
                "Pay Ksh " + billPaymentAsyncRequest.getTransactionAmount() +
                        " to "
                        + billPaymentAsyncRequest.getRecipientAccountNumber()
                        + "bill number");
        map.put("amount", String.valueOf(amountPaid));
        map.put("category", "Expense");
        map.put("transactionFee", transactionFee);
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("beneficiaryAccount", billPaymentAsyncRequest.getSenderAccountNumber());
        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }
        return CompletableFuture.completedFuture(billPaymentAsyncRequest);
    }
}
