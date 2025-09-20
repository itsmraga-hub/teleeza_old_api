package com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TransactionsEntity;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.AllTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.requests.InternalTvPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.requests.TvPaymentAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.responses.TvPaymentResponse;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.entity.TvPaymentEntity;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.repository.TvPaymentRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.service.TvPaymentApi;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/teleeza-wallet")
public class TvPaymentController {
    private final TvPaymentApi tvPaymentApi;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final TvPaymentRepository tvPaymentRepository;
    private final AllTransactionsRepository allTransactionsRepository;
    private final TransactionsRepository transactionsRepository;
    private final CustomerRegistrationRepository customerRegistrationRepository;

    public TvPaymentController(TvPaymentApi tvPaymentApi, ObjectMapper objectMapper, OkHttpClient okHttpClient, TvPaymentRepository tvPaymentRepository, AllTransactionsRepository allTransactionsRepository, TransactionsRepository transactionsRepository, CustomerRegistrationRepository customerRegistrationRepository) {
        this.tvPaymentApi = tvPaymentApi;
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
        this.tvPaymentRepository = tvPaymentRepository;
        this.allTransactionsRepository = allTransactionsRepository;
        this.transactionsRepository = transactionsRepository;
        this.customerRegistrationRepository = customerRegistrationRepository;
    }

    @PostMapping("/pay-tv")
    public ResponseEntity<TvPaymentResponse> payTv(
            @RequestBody InternalTvPaymentRequest internalTvPaymentRequest
    ) {
        TvPaymentResponse response = tvPaymentApi.payForTv(internalTvPaymentRequest);
        TvPaymentEntity tvPaymentEntity = new TvPaymentEntity();
        tvPaymentEntity.setMerchantCode("669994");
        tvPaymentEntity.setServiceCode(internalTvPaymentRequest.getServiceCode());
        tvPaymentEntity.setMobileNumber(internalTvPaymentRequest.getMobileNumber());
        tvPaymentEntity.setBeneficiaryAccountNumber(internalTvPaymentRequest.getBeneficiaryAccountNumber());
        tvPaymentEntity.setAmount(String.valueOf(internalTvPaymentRequest.getAmount()));
        tvPaymentEntity.setAccountNumber(internalTvPaymentRequest.getAccountNumber());
        tvPaymentEntity.setStatusCode(response.getStatusCode());
        tvPaymentRepository.save(tvPaymentEntity);

        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setBeneficiaryAccNumber(internalTvPaymentRequest.getBeneficiaryAccountNumber());
        transactionsEntity.setAmount((double) internalTvPaymentRequest.getAmount());
        transactionsEntity.setTransAmount((double) internalTvPaymentRequest.getAmount());
        transactionsEntity.setMerchantTransactionRef("TV");
        transactionsEntity.setMerchantRequestId("TV");
        transactionsEntity.setIsTransactionType(false);
        transactionsEntity.setReason("Pay for Tv");
        allTransactionsRepository.save(transactionsEntity);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tv-validation")
//    @ApiIgnore
    public ResponseEntity<TvPaymentAsyncRequest> validateTvPayment(
            @RequestBody TvPaymentAsyncRequest tvPaymentAsyncRequest
    ) {

        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(tvPaymentAsyncRequest.getSenderAccountNumber());

        String tvAmount = tvPaymentAsyncRequest.getAmount();
        BigInteger amountPaid = new BigDecimal(tvAmount).toBigInteger();

        Transactions transactionsEntity = new Transactions();
        transactionsEntity.setMerchantRequestId(tvPaymentAsyncRequest.getServiceCode()); // add service code in place of merchant requets id
        transactionsEntity.setCheckoutRequestId("");
        transactionsEntity.setResultCode(0);
        transactionsEntity.setResultDesc(tvPaymentAsyncRequest.getMessage());
        transactionsEntity.setTransactionAmount(tvPaymentAsyncRequest.getAmount());
        transactionsEntity.setTransactionDate(tvPaymentAsyncRequest.getTransTime());
        transactionsEntity.setBillRefNumber(" ");
        transactionsEntity.setMerchantCode("669994");
        transactionsEntity.setRecipientAccountNumber(tvPaymentAsyncRequest.getAccountNumber());
        transactionsEntity.setThirdPartyId("");
        transactionsEntity.setBeneficiaryAccNumber(tvPaymentAsyncRequest.getSenderAccountNumber());
        transactionsEntity.setSenderAccountNumber(tvPaymentAsyncRequest.getSenderAccountNumber());
        transactionsEntity.setIsTransactionType(false);
        transactionsEntity.setSourceChannel("");
        transactionsEntity.setDestinationChannel("");
        transactionsEntity.setRecipientName("");
        transactionsEntity.setReason(tvPaymentAsyncRequest.getVoucherType());
        transactionsEntity.setServiceCode(tvPaymentAsyncRequest.getServiceCode());
        transactionsEntity.setVoucherType(tvPaymentAsyncRequest.getVoucherType());
        transactionsEntity.setSenderName(customerEntity.getDisplayName());
        transactionsEntity.setRecipientName(tvPaymentAsyncRequest.getServiceCode());
        transactionsEntity.setPin("");
        transactionsEntity.setUnits("");
        transactionsRepository.save(transactionsEntity);

        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        // update Kokotoa
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", tvPaymentAsyncRequest.getServiceCode());
        map.put("description", "Pay for "+tvPaymentAsyncRequest.getServiceCode());//+internalCustomerToCustomerRequest.getRecipientBeneficiaryAccountNumber()
        map.put("amount", String.valueOf(amountPaid));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("beneficiaryAccount", tvPaymentAsyncRequest.getSenderAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                "https://kokotoa.teleeza.africa/api/callback",
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(tvPaymentAsyncRequest);
    }
}
