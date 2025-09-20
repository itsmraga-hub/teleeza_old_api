package com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.requests.InternalPostPaidBillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.requests.PostPaidAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.dtos.responses.PostPaidBillPaymentResponse;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.entity.PostPaidEntity;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.repository.PostPaidTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.service.PostPaidApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/teleeza-wallet/")
public class PostPaidController {

    private final PostPaidApi postPaidApi;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final TransactionsRepository transactionsRepository;
    private final KokotoaConfig kokotoaConfig;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private PostPaidTransactionsRepository postPaidTransactionsRepository;

    public PostPaidController(PostPaidApi postPaidApi,
                              ObjectMapper objectMapper,
                              OkHttpClient okHttpClient,
                              TransactionsRepository transactionsRepository,
                              KokotoaConfig kokotoaConfig, CustomerRegistrationRepository customerRegistrationRepository) {
        this.postPaidApi = postPaidApi;
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
        this.transactionsRepository = transactionsRepository;
        this.kokotoaConfig = kokotoaConfig;
        this.customerRegistrationRepository = customerRegistrationRepository;
    }

    @PostMapping("/postpaid-bills")
    public ResponseEntity<PostPaidBillPaymentResponse> postPaidPayment(
            @RequestBody InternalPostPaidBillPaymentRequest internalPostPaidBillPaymentRequest
    ) throws JsonProcessingException {
        PostPaidBillPaymentResponse response = postPaidApi.lipaPostPaidBills(internalPostPaidBillPaymentRequest);

        String postPaidResponse = objectMapper.writeValueAsString(response);
        PostPaidBillPaymentResponse postPaidBillPaymentResponse = objectMapper.readValue(postPaidResponse, PostPaidBillPaymentResponse.class);

        // save transaction attempts
        PostPaidEntity postPaidEntity = new PostPaidEntity();
        postPaidEntity.setBeneficiaryAccountNumber(internalPostPaidBillPaymentRequest.getBeneficiaryAccountNumber());
        postPaidEntity.setMeterNumber(internalPostPaidBillPaymentRequest.getAccountNumber());
        postPaidEntity.setMobileNumber(internalPostPaidBillPaymentRequest.getMobileNumber());
        postPaidEntity.setServiceOption(internalPostPaidBillPaymentRequest.getServiceCode());
        postPaidEntity.setCurrency("KES");
        postPaidEntity.setAmount(internalPostPaidBillPaymentRequest.getAmount());
        postPaidEntity.setStatusCode(postPaidBillPaymentResponse.getStatusCode());
        postPaidEntity.setMessage(postPaidBillPaymentResponse.getMessage());
        postPaidTransactionsRepository.save(postPaidEntity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/postpaid-validation")
    public ResponseEntity<PostPaidAsyncRequest> postPaidValidation(
            @RequestBody PostPaidAsyncRequest postPaidAsyncRequest
    ){
//        Transactions transactions = new Transactions();
        Transactions transactionsEntity = new Transactions();
        transactionsEntity.setMerchantRequestId(postPaidAsyncRequest.getServiceCode()); // add service code in place of merchant requets id
        transactionsEntity.setCheckoutRequestId("");
        transactionsEntity.setResultCode(0);
        transactionsEntity.setResultDesc(postPaidAsyncRequest.getMessage());
        transactionsEntity.setTransactionAmount(String.valueOf(postPaidAsyncRequest.getAmount()));
        transactionsEntity.setTransactionDate(postPaidAsyncRequest.getTransTime());
        transactionsEntity.setBillRefNumber(" ");
        transactionsEntity.setMerchantCode("669994");
        transactionsEntity.setRecipientAccountNumber(postPaidAsyncRequest.getAccountNumber());
        transactionsEntity.setThirdPartyId("");
        transactionsEntity.setBeneficiaryAccNumber(postPaidAsyncRequest.getSenderAccountNumber());
        transactionsEntity.setSenderAccountNumber(postPaidAsyncRequest.getSenderAccountNumber());
        transactionsEntity.setIsTransactionType(false);
        transactionsEntity.setSourceChannel("");
        transactionsEntity.setDestinationChannel("");
        transactionsEntity.setRecipientName("");
        transactionsEntity.setReason(postPaidAsyncRequest.getServiceCode());
        transactionsEntity.setServiceCode(postPaidAsyncRequest.getServiceCode());
        transactionsEntity.setVoucherType(postPaidAsyncRequest.getVoucherType());
        transactionsEntity.setPin("");
        transactionsEntity.setUnits("");
        transactionsRepository.save(transactionsEntity);

        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(postPaidAsyncRequest.getSenderAccountNumber());

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        // update Kokotoa
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", "PostPaid: KPLC");
        map.put("description", "Buy Airtime for " + postPaidAsyncRequest.getAccountNumber());//+internalCustomerToCustomerRequest.getRecipientBeneficiaryAccountNumber()
        map.put("amount", String.valueOf(postPaidAsyncRequest.getAmount()));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("beneficiaryAccount", postPaidAsyncRequest.getSenderAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(postPaidAsyncRequest);
    }
}
