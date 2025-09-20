package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.CustomerResponse;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests.InternalMobileMoneyTransferRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests.MobileMoneyTransferAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.repository.MobileMoneyTransactionRepository;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.responses.CustomerToMobileMoneyResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.entity.MobileMoneyTransactionEntity;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TransactionsEntity;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/v1/teleeza-wallet")
@Slf4j
public class MobileMoneyTransferController {
    private final SasaPayApi sasapayApi;
    private final ObjectMapper objectMapper;
    private final MobileMoneyTransactionRepository repository;
    private final AllTransactionsRepository allTransactionsRepository;
    private final TransactionsRepository transactionsRepository;
    private final KokotoaConfig kokotoaConfig;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;

    public MobileMoneyTransferController(SasaPayApi sasapayApi,
                                         ObjectMapper objectMapper,
                                         MobileMoneyTransactionRepository repository,
                                         AllTransactionsRepository allTransactionsRepository,
                                         TransactionsRepository transactionsRepository, KokotoaConfig kokotoaConfig) {
        this.sasapayApi = sasapayApi;
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.allTransactionsRepository = allTransactionsRepository;
        this.transactionsRepository = transactionsRepository;
        this.kokotoaConfig = kokotoaConfig;
    }

    @PostMapping(path = "/customer-to-mobile-money", produces = "application/json")
    public ResponseEntity<CustomerToMobileMoneyResponse> performCustomerToMobileMoneyTransfer(
            @RequestBody InternalMobileMoneyTransferRequest internalMobileMoneyTransferRequest) throws JsonProcessingException {

        CustomerToMobileMoneyResponse sendToMobileResponse =
                sasapayApi.customerToMobileMoney(internalMobileMoneyTransferRequest);
        String response = objectMapper.writeValueAsString(sendToMobileResponse);
        CustomerToMobileMoneyResponse mobileMoneyResponse = objectMapper.readValue(response, CustomerToMobileMoneyResponse.class);

        // save to transactions attempts table  `all transactions`
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setNetworkCode(internalMobileMoneyTransferRequest.getChannelCode());
        transactionsEntity.setAmount(Double.valueOf(internalMobileMoneyTransferRequest.getAmount()));
        transactionsEntity.setCustomerMobile(internalMobileMoneyTransferRequest.getMobileOperatorNumber());
        transactionsEntity.setBeneficiaryAccNumber(internalMobileMoneyTransferRequest.getBeneficiaryAccountNumber());
        transactionsEntity.setMerchantCode("669994");
        transactionsEntity.setTransactionFee(0);
        transactionsEntity.setMerchantTransactionRef(mobileMoneyResponse.getTransactionReference());
        transactionsEntity.setTransactionReference(mobileMoneyResponse.getReferenceNumber());
        transactionsEntity.setResultDesc(mobileMoneyResponse.getMessage());
        transactionsEntity.setResultCode(mobileMoneyResponse.getStatusCode());
        allTransactionsRepository.save(transactionsEntity);

        // save mobile money transactions attempts

        MobileMoneyTransactionEntity mobileMoneyTransactionEntity = new MobileMoneyTransactionEntity();
        mobileMoneyTransactionEntity.setBeneficiaryAccNumber(internalMobileMoneyTransferRequest.getBeneficiaryAccountNumber());
        mobileMoneyTransactionEntity.setAmount(internalMobileMoneyTransferRequest.getAmount());
        mobileMoneyTransactionEntity.setChannelCode(internalMobileMoneyTransferRequest.getChannelCode());
        mobileMoneyTransactionEntity.setMerchantCode("669994");
        mobileMoneyTransactionEntity.setTransactionFee(0);
        mobileMoneyTransactionEntity.setMobileOperatorNumber(internalMobileMoneyTransferRequest.getMobileOperatorNumber());
        mobileMoneyTransactionEntity.setReason("Send money to " + internalMobileMoneyTransferRequest.getMobileOperatorNumber());
        mobileMoneyTransactionEntity.setResultCode(mobileMoneyTransactionEntity.getResultCode());
        mobileMoneyTransactionEntity.setReferenceNumber(mobileMoneyResponse.getReferenceNumber());
        mobileMoneyTransactionEntity.setTransactionReference(mobileMoneyTransactionEntity.getTransactionReference());
        repository.save(mobileMoneyTransactionEntity);

        return ResponseEntity.ok(sendToMobileResponse);
    }

    @PostMapping("/mobilemoney-transfer-validation")
//    @ApiIgnore
    public ResponseEntity<MobileMoneyTransferAsyncRequest> validateMobileMoneyTransfer(
            @RequestBody MobileMoneyTransferAsyncRequest mobileMoneyTransferAsyncRequest
    ) {

        String amount = mobileMoneyTransferAsyncRequest.getTransactionAmount();
        BigInteger bigIntAmount = new BigDecimal(amount).toBigInteger();

        CustomerEntity customer =
                customerRegistrationRepository.findByCustomerAccountNumber(mobileMoneyTransferAsyncRequest.getSenderAccountNumber());

        Transactions transactions = new Transactions();

        String destinationChannel = mobileMoneyTransferAsyncRequest.getDestinationChannel();
        double transactedAmount = Double.parseDouble(mobileMoneyTransferAsyncRequest.getTransactionAmount());
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

        // Get customer balance
        String url = String.format("%s?CustomerAccountNumber=%s", "https://api.sasapay.app//api/v1/waas/customers/detail/", mobileMoneyTransferAsyncRequest.getSenderAccountNumber());

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



        if (destinationChannel.equals("63902")) {
            log.info("Transactions Response {}", transactions);
            transactions.setCheckoutRequestId(mobileMoneyTransferAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(mobileMoneyTransferAsyncRequest.getResultCode());
            transactions.setResultDesc(mobileMoneyTransferAsyncRequest.getResultDesc());
            transactions.setMerchantCode(mobileMoneyTransferAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(mobileMoneyTransferAsyncRequest.getTransactionAmount());
            transactions.setMerchantAccountBalance(mobileMoneyTransferAsyncRequest.getMerchantAccountBalance());
            transactions.setMerchantTransactionReference(mobileMoneyTransferAsyncRequest.getMerchantTransactionReference());
            transactions.setTransactionDate(mobileMoneyTransferAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(mobileMoneyTransferAsyncRequest.getRecipientAccountNumber());
            transactions.setDestinationChannel("M-Pesa");
            transactions.setBeneficiaryAccNumber(mobileMoneyTransferAsyncRequest.getSenderAccountNumber());
            transactions.setSourceChannel(mobileMoneyTransferAsyncRequest.getSourceChannel());
            transactions.setSasaPayTransactionId(mobileMoneyTransferAsyncRequest.getSasaPayTransactionID());
            transactions.setRecipientName(mobileMoneyTransferAsyncRequest.getRecipientName());
            transactions.setSenderName(customer.getDisplayName());
            transactions.setReason("Send Money: M-Pesa");
            transactions.setSenderAccountNumber(mobileMoneyTransferAsyncRequest.getSenderAccountNumber());
            transactions.setIsTransactionType(false);
            transactions.setTransactionFee(transactionFee);
            transactions.setSenderName(customer.getDisplayName());
            transactions.setMerchantRequestId(mobileMoneyTransferAsyncRequest.getMerchantRequestID());
            transactions.setCustomerBalance(Objects.requireNonNull(response.getBody()).getResult().getCustomerBalance());
            transactionsRepository.save(transactions);
        }

        if (destinationChannel.equals("63903 ")) {
            log.info("Transactions Response {}", transactions);
            transactions.setCheckoutRequestId(mobileMoneyTransferAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(mobileMoneyTransferAsyncRequest.getResultCode());
            transactions.setResultDesc(mobileMoneyTransferAsyncRequest.getResultDesc());
            transactions.setMerchantCode(mobileMoneyTransferAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(mobileMoneyTransferAsyncRequest.getTransactionAmount());
            transactions.setMerchantAccountBalance(mobileMoneyTransferAsyncRequest.getMerchantAccountBalance());
            transactions.setMerchantTransactionReference(mobileMoneyTransferAsyncRequest.getMerchantTransactionReference());
            transactions.setTransactionDate(mobileMoneyTransferAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(mobileMoneyTransferAsyncRequest.getRecipientAccountNumber());
            transactions.setDestinationChannel("AirtelMoney");
            transactions.setBeneficiaryAccNumber(mobileMoneyTransferAsyncRequest.getSenderAccountNumber());
            transactions.setSourceChannel(mobileMoneyTransferAsyncRequest.getSourceChannel());
            transactions.setSasaPayTransactionId(mobileMoneyTransferAsyncRequest.getSasaPayTransactionID());
            transactions.setRecipientName(mobileMoneyTransferAsyncRequest.getRecipientName());
            transactions.setReason("Send Money: AirtelMoney");
            transactions.setSenderAccountNumber(mobileMoneyTransferAsyncRequest.getSenderAccountNumber());
            transactions.setIsTransactionType(false);
            transactions.setTransactionFee(transactionFee);
            transactions.setSenderName(customer.getDisplayName());
            transactions.setMerchantRequestId(mobileMoneyTransferAsyncRequest.getMerchantRequestID());
            transactions.setCustomerBalance(Objects.requireNonNull(response.getBody()).getResult().getCustomerBalance());
            transactionsRepository.save(transactions);
        }
        if (destinationChannel.equals("63907")) {
            log.info("Transactions Response {}", transactions);
            transactions.setCheckoutRequestId(mobileMoneyTransferAsyncRequest.getCheckoutRequestID());
            transactions.setResultCode(mobileMoneyTransferAsyncRequest.getResultCode());
            transactions.setResultDesc(mobileMoneyTransferAsyncRequest.getResultDesc());
            transactions.setMerchantCode(mobileMoneyTransferAsyncRequest.getMerchantCode());
            transactions.setTransactionAmount(mobileMoneyTransferAsyncRequest.getTransactionAmount());
            transactions.setMerchantAccountBalance(mobileMoneyTransferAsyncRequest.getMerchantAccountBalance());
            transactions.setMerchantTransactionReference(mobileMoneyTransferAsyncRequest.getMerchantTransactionReference());
            transactions.setTransactionDate(mobileMoneyTransferAsyncRequest.getTransactionDate());
            transactions.setRecipientAccountNumber(mobileMoneyTransferAsyncRequest.getRecipientAccountNumber());
            transactions.setDestinationChannel("T-Kash");
            transactions.setBeneficiaryAccNumber(mobileMoneyTransferAsyncRequest.getSenderAccountNumber());
            transactions.setSourceChannel(mobileMoneyTransferAsyncRequest.getSourceChannel());
            transactions.setSasaPayTransactionId(mobileMoneyTransferAsyncRequest.getSasaPayTransactionID());
            transactions.setRecipientName(mobileMoneyTransferAsyncRequest.getRecipientName());
            transactions.setReason("Send Money: T-Kash");
            transactions.setSenderAccountNumber(mobileMoneyTransferAsyncRequest.getSenderAccountNumber());
            transactions.setIsTransactionType(false);
            transactions.setTransactionFee(transactionFee);
            transactions.setSenderName(customer.getDisplayName());
            transactions.setMerchantRequestId(mobileMoneyTransferAsyncRequest.getMerchantRequestID());
            transactions.setCustomerBalance(Objects.requireNonNull(response.getBody()).getResult().getCustomerBalance());
            transactionsRepository.save(transactions);
        }

        //update record to kokotoa
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", "Send Money");
        map.put("description", "Send money to mobile number " + mobileMoneyTransferAsyncRequest.getRecipientAccountNumber());
        map.put("amount", String.valueOf(bigIntAmount));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", "");
        map.put("transactionFee",transactionFee);
        map.put("beneficiaryAccount", mobileMoneyTransferAsyncRequest.getSenderAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return ResponseEntity.ok(mobileMoneyTransferAsyncRequest);
    }
}
