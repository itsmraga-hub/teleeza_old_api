package com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TransactionsEntity;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.AllTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.requests.AirtimeAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.requests.InternalAirtimeRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.responses.AirtimeResponse;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.entity.AirtimeEntity;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.repository.AirtimeRepository;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.service.AirtimeApi;
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
//import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/v1/teleeza-wallet")
public class AirtimeController {
    private final AirtimeApi airtimeApi;
    private final ObjectMapper objectMapper;

    private final AirtimeRepository repository;
    private final AllTransactionsRepository allTransactionsRepository;
    private final OkHttpClient okHttpClient;
    private final TransactionsRepository transactionsRepository;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final KokotoaConfig kokotoaConfig;
    @Autowired
    private AirtimeRepository airtimeRepository;
    @Autowired
    private RestTemplate restTemplate;

    public AirtimeController(AirtimeApi airtimeApi,
                             ObjectMapper objectMapper,
                             AirtimeRepository repository,
                             AllTransactionsRepository allTransactionsRepository,
                             OkHttpClient okHttpClient,
                             TransactionsRepository transactionsRepository,
                             CustomerRegistrationRepository customerRegistrationRepository, KokotoaConfig kokotoaConfig) {
        this.airtimeApi = airtimeApi;
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.allTransactionsRepository = allTransactionsRepository;
        this.okHttpClient = okHttpClient;
        this.transactionsRepository = transactionsRepository;
        this.customerRegistrationRepository = customerRegistrationRepository;
        this.kokotoaConfig = kokotoaConfig;
    }

    @PostMapping("/buy-airtime")
    public ResponseEntity<AirtimeResponse> buyAirtime(
            @RequestBody InternalAirtimeRequest internalAirtimeRequest
    ) throws JsonProcessingException {
        AirtimeResponse response = airtimeApi.buyAirtime(internalAirtimeRequest);

        // update record to all transactions
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setBeneficiaryAccNumber(internalAirtimeRequest.getBeneficiaryAccountNumber());
        transactionsEntity.setAmount((double) internalAirtimeRequest.getAmount());
        transactionsEntity.setTransAmount((double) internalAirtimeRequest.getAmount());
        transactionsEntity.setMerchantTransactionRef("AIRTIME");
        transactionsEntity.setMerchantRequestId("AIRTIME");
        transactionsEntity.setIsTransactionType(false);
        transactionsEntity.setReason("Buy Airtime");
        allTransactionsRepository.save(transactionsEntity);

        AirtimeEntity airtimeEntity = new AirtimeEntity();
        airtimeEntity.setMerchantCode("669994");
        airtimeEntity.setNetworkCode(internalAirtimeRequest.getNetworkCode());
        airtimeEntity.setBeneficiaryAccNumber(internalAirtimeRequest.getBeneficiaryAccountNumber());
        airtimeEntity.setAccountNumber(internalAirtimeRequest.getMobileNumber());
        airtimeEntity.setAmount(String.valueOf(internalAirtimeRequest.getAmount()));
        airtimeRepository.save(airtimeEntity);


//        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/airtime-validation")
//    @ApiIgnore
    public ResponseEntity<AirtimeAsyncRequest> validateAirtimePurchase(@RequestBody AirtimeAsyncRequest airtimeAsyncRequest) throws ParseException {


        CustomerEntity customerEntity = customerRegistrationRepository
                .findByCustomerAccountNumber(airtimeAsyncRequest.getSenderAccountNumber());

        if(Objects.equals(airtimeAsyncRequest.getNetworkCode(), "63902")){
            Transactions transactionsEntity = new Transactions();
            transactionsEntity.setMerchantRequestId("Safaricom"); // add service code in place of merchant requets id
            transactionsEntity.setCheckoutRequestId("");
            transactionsEntity.setResultCode(0);
            transactionsEntity.setResultDesc(airtimeAsyncRequest.getMessage());
            transactionsEntity.setTransactionAmount(String.valueOf(airtimeAsyncRequest.getAmount()));
            transactionsEntity.setTransactionDate(airtimeAsyncRequest.getTransTime());
            transactionsEntity.setBillRefNumber("");
            transactionsEntity.setMerchantCode("669994");
            transactionsEntity.setRecipientAccountNumber(airtimeAsyncRequest.getAccountNumber());
            transactionsEntity.setThirdPartyId("");
            transactionsEntity.setBeneficiaryAccNumber(airtimeAsyncRequest.getSenderAccountNumber());
            transactionsEntity.setSenderAccountNumber(airtimeAsyncRequest.getSenderAccountNumber());
            transactionsEntity.setIsTransactionType(false);
            transactionsEntity.setSourceChannel("");
            transactionsEntity.setDestinationChannel("");
            transactionsEntity.setRecipientName("");
            transactionsEntity.setReason("Airtime");
            transactionsEntity.setSenderName(customerEntity.getDisplayName());
            transactionsEntity.setRecipientName("");
            transactionsEntity.setServiceCode(airtimeAsyncRequest.getServiceCode());
            transactionsEntity.setVoucherType(airtimeAsyncRequest.getVoucherType());
            transactionsEntity.setPin("");
            transactionsEntity.setUnits("");
            transactionsRepository.save(transactionsEntity);
        }
        if(Objects.equals(airtimeAsyncRequest.getNetworkCode(), "63903")){
            Transactions transactionsEntity = new Transactions();
            transactionsEntity.setMerchantRequestId("Airtel"); // add service code in place of merchant requets id
            transactionsEntity.setCheckoutRequestId("");
            transactionsEntity.setResultCode(0);
            transactionsEntity.setResultDesc(airtimeAsyncRequest.getMessage());
            transactionsEntity.setTransactionAmount(String.valueOf(airtimeAsyncRequest.getAmount()));
            transactionsEntity.setTransactionDate(airtimeAsyncRequest.getTransTime());
            transactionsEntity.setBillRefNumber("");
            transactionsEntity.setMerchantCode("669994");
            transactionsEntity.setRecipientAccountNumber(airtimeAsyncRequest.getAccountNumber());
            transactionsEntity.setThirdPartyId("");
            transactionsEntity.setBeneficiaryAccNumber(airtimeAsyncRequest.getSenderAccountNumber());
            transactionsEntity.setSenderAccountNumber(airtimeAsyncRequest.getSenderAccountNumber());
            transactionsEntity.setIsTransactionType(false);
            transactionsEntity.setSourceChannel("");
            transactionsEntity.setDestinationChannel("");
            transactionsEntity.setRecipientName("");
            transactionsEntity.setReason("Airtime");
            transactionsEntity.setSenderName(customerEntity.getDisplayName());
            transactionsEntity.setRecipientName("");
            transactionsEntity.setServiceCode(airtimeAsyncRequest.getServiceCode());
            transactionsEntity.setVoucherType(airtimeAsyncRequest.getVoucherType());
            transactionsEntity.setPin("");
            transactionsEntity.setUnits("");
            transactionsRepository.save(transactionsEntity);
        }

        if(Objects.equals(airtimeAsyncRequest.getNetworkCode(), "63907")){
            Transactions transactionsEntity = new Transactions();
            transactionsEntity.setMerchantRequestId("Telkom"); // add service code in place of merchant requets id
            transactionsEntity.setCheckoutRequestId("");
            transactionsEntity.setResultCode(0);
            transactionsEntity.setResultDesc(airtimeAsyncRequest.getMessage());
            transactionsEntity.setTransactionAmount(String.valueOf(airtimeAsyncRequest.getAmount()));
            transactionsEntity.setTransactionDate(airtimeAsyncRequest.getTransTime());
            transactionsEntity.setBillRefNumber("");
            transactionsEntity.setMerchantCode("669994");
            transactionsEntity.setRecipientAccountNumber(airtimeAsyncRequest.getAccountNumber());
            transactionsEntity.setThirdPartyId("");
            transactionsEntity.setBeneficiaryAccNumber(airtimeAsyncRequest.getSenderAccountNumber());
            transactionsEntity.setSenderAccountNumber(airtimeAsyncRequest.getSenderAccountNumber());
            transactionsEntity.setIsTransactionType(false);
            transactionsEntity.setSourceChannel("");
            transactionsEntity.setDestinationChannel("");
            transactionsEntity.setRecipientName("");
            transactionsEntity.setReason("Airtime");
            transactionsEntity.setSenderName(customerEntity.getDisplayName());
            transactionsEntity.setRecipientName("");
            transactionsEntity.setServiceCode("Telkom");
            transactionsEntity.setVoucherType(airtimeAsyncRequest.getVoucherType());
            transactionsEntity.setPin("");
            transactionsEntity.setUnits("");
            transactionsRepository.save(transactionsEntity);
        }


        String customerPhone = customerEntity.getMobileNumber();
        String phone = "" + customerPhone.substring(1);

        // update Kokotoa
        Map<String, String> map = new HashMap<>();
        map.put("name", "Buy Airtime");
        map.put("description", "Buy Airtime for " + airtimeAsyncRequest.getAccountNumber());//+internalCustomerToCustomerRequest.getRecipientBeneficiaryAccountNumber()
        map.put("amount", String.valueOf(airtimeAsyncRequest.getAmount()));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", phone);
        map.put("transactionFee","0");
        map.put("beneficiaryAccount", airtimeAsyncRequest.getSenderAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return new ResponseEntity<>(airtimeAsyncRequest, HttpStatus.OK);
    }
}
