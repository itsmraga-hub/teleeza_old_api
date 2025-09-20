package com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.BankTransferRepository;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests.BankTransferAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests.InternalBankTransfer;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.response.BankTransferResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.responses.BanksListResponse;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.AllTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
//import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.teleeza.wallet.teleeza.utils.Constants.AUTHORIZATION_HEADER_STRING;
import static com.teleeza.wallet.teleeza.utils.Constants.BEARER_AUTH_STRING;

@RestController
@RequestMapping("/v1/teleeza-wallet")
@Slf4j
public class BankTransferController {
    private final SasaPayApi sasaPayApi;
    private final ObjectMapper objectMapper;
    private final AllTransactionsRepository allTransactionsRepository;

    private final TransactionsRepository transactionsRepository;
    private final KokotoaConfig kokotoaConfig;

    private final OkHttpClient okHttpClient;
    @Autowired
    private BankTransferRepository bankTransferRepository;

    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;

    public BankTransferController(SasaPayApi sasaPayApi,
                                  ObjectMapper objectMapper,
                                  AllTransactionsRepository allTransactionsRepository,
                                  TransactionsRepository transactionsRepository, KokotoaConfig kokotoaConfig, OkHttpClient okHttpClient) {
        this.sasaPayApi = sasaPayApi;
        this.objectMapper = objectMapper;
        this.allTransactionsRepository = allTransactionsRepository;
        this.transactionsRepository = transactionsRepository;
        this.kokotoaConfig = kokotoaConfig;
        this.okHttpClient = okHttpClient;
    }

    @PostMapping(path = "/customer-to-bank", produces = "application/json")
    public ResponseEntity<BankTransferResponse> sendmoneyToBank(@RequestBody InternalBankTransfer internalBankTransfer)
            throws JsonProcessingException {
        BankTransferResponse sendToBank = sasaPayApi.sendToBank(internalBankTransfer);

        String response = objectMapper.writeValueAsString(sendToBank);
        BankTransferResponse bankResponse = objectMapper.readValue(response, BankTransferResponse.class);

//        log.info("deserialized body {}", bankResponse);
//        log.info("deserialized body {}", bankResponse.getMessage());

        // save transaction attempt
//        BankTransferEntity bankTransfer = new BankTransferEntity();
//        bankTransfer.setBeneficiaryAccountNumber(internalBankTransfer.getBeneficiaryAccountNumber());
//        bankTransfer.setReceiverNumber(internalBankTransfer.getReceiverNumber());
//        bankTransfer.setChannelCode(internalBankTransfer.getChannelCode());
//        bankTransfer.setReason(internalBankTransfer.getReason());
//        bankTransfer.setAmount(internalBankTransfer.getAmount());
//        bankTransfer.setTransactionReference(bankResponse.getTransactionReference());
//        bankTransfer.setStatusCode(bankResponse.getStatusCode());
//        bankTransfer.setReferenceNumber(bankResponse.getReferenceNumber());
//        bankTransfer.setResultDescription(bankResponse.getMessage());
//        bankTransferRepository.save(bankTransfer);

        // save record to all transactions table
//        TransactionsEntity transactionsEntity = new TransactionsEntity();
//        transactionsEntity.setBeneficiaryAccNumber(internalBankTransfer.getBeneficiaryAccountNumber());
//        transactionsEntity.setReason(internalBankTransfer.getReason());
//        transactionsEntity.setRecipientAccountNumber(internalBankTransfer.getReceiverNumber());
//        transactionsEntity.setNetworkCode(internalBankTransfer.getChannelCode());
//        transactionsEntity.setAmount(Double.valueOf(internalBankTransfer.getAmount()));
//        transactionsEntity.setMerchantCode("669994");
//        transactionsEntity.setReason("Bank Transfer");
//        transactionsEntity.setResultCode(bankResponse.getStatusCode());
//        transactionsEntity.setMerchantRequestId(bankResponse.getTransactionReference());
//        transactionsEntity.setTransactionReference(bankResponse.getReferenceNumber());
//        transactionsEntity.setResultDesc(bankResponse.getMessage());
//        allTransactionsRepository.save(transactionsEntity);
        return ResponseEntity.ok(sendToBank);
    }

    @PostMapping("/bank-transfer-validation")
//    @ApiIgnore
    public ResponseEntity<BankTransferAsyncRequest> validateBankTransfers(
            @RequestBody BankTransferAsyncRequest bankTransferAsyncRequest
    ){
        CustomerEntity customer =
                customerRegistrationRepository.findByCustomerAccountNumber(bankTransferAsyncRequest.getSenderAccountNumber());

        log.info("Customer Info  :  {}",customer.getCustomerAccountNumber());


        double transactedAmount = Double.parseDouble(bankTransferAsyncRequest.getTransactionAmount());
        int amountTransacted = (int) transactedAmount;
        String transactionFee = amountTransacted <= 100 ? "0" :
                amountTransacted >= 101 && amountTransacted <= 500 ? "10" :
                        amountTransacted >= 501 && amountTransacted <= 5000 ? "28" :
                                amountTransacted >= 5001 && amountTransacted <= 10000 ? "42" :
                                        amountTransacted >= 10001 && amountTransacted <= 20000 ? "58" :
                                                amountTransacted >= 20001 && amountTransacted <= 50000 ? "69" :
                                                        amountTransacted >= 50001 && amountTransacted <= 70000 ? "99" :
                                                                amountTransacted >= 70001 && amountTransacted <= 150000 ? "99" :
                                                                        amountTransacted >= 150001 && amountTransacted <= 999999 ? "149" : "";

        String paidToBank = bankTransferAsyncRequest.getTransactionAmount();
        BigInteger sentToBank = new BigDecimal(paidToBank).toBigInteger();

        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(bankTransferAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(bankTransferAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(bankTransferAsyncRequest.getResultCode());
        transactions.setResultDesc(bankTransferAsyncRequest.getResultDesc());
        transactions.setMerchantCode(bankTransferAsyncRequest.getMerchantCode());
        transactions.setTransactionAmount(bankTransferAsyncRequest.getTransactionAmount());
        transactions.setMerchantAccountBalance(bankTransferAsyncRequest.getMerchantAccountBalance());
        transactions.setMerchantTransactionReference(bankTransferAsyncRequest.getMerchantTransactionReference());
        transactions.setTransactionDate(bankTransferAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(bankTransferAsyncRequest.getRecipientAccountNumber());
        transactions.setDestinationChannel(bankTransferAsyncRequest.getDestinationChannel());
        transactions.setSourceChannel(bankTransferAsyncRequest.getSourceChannel());
        transactions.setSasaPayTransactionId(bankTransferAsyncRequest.getSasaPayTransactionID());
        transactions.setRecipientName(bankTransferAsyncRequest.getRecipientName());
        transactions.setBeneficiaryAccNumber(bankTransferAsyncRequest.getSenderAccountNumber());
        transactions.setSenderAccountNumber(bankTransferAsyncRequest.getSenderAccountNumber());
        transactions.setBillRefNumber("");
        transactions.setSenderName(customer.getDisplayName());
        transactions.setRecipientName("");
        transactions.setThirdPartyId("");
        transactions.setTransactionFee(transactionFee);
        transactions.setReason("Bank Transfer");
        transactions.setIsTransactionType(false);
        transactionsRepository.save(transactions);

        //update record to kokotoa
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", "Wallet");
        map.put("description", "Send money to bank from account "+bankTransferAsyncRequest.getSenderAccountNumber());
        map.put("amount", String.valueOf(sentToBank));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", "");
        map.put("beneficiaryAccount", bankTransferAsyncRequest.getSenderAccountNumber());
        map.put("transactionFee",transactionFee);

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);

        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        return new ResponseEntity<>(bankTransferAsyncRequest,HttpStatus.OK);

    }

    @GetMapping("/banks-list")
    public ResponseEntity<BanksListResponse> getBanksList() {
        HttpUrl bankListUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.sasapay.app")
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment("waas")
                .addPathSegment("channel-codes")
                .build();

        Request request = new Request.Builder()
                .url(bankListUrl)
                .addHeader("Accept", "application/json")
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, sasaPayApi.getAccessToken().getAccessToken()))
                .method("GET", null)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            BanksListResponse result = objectMapper.readValue(response.body().string(), BanksListResponse.class);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Banks", Lists.reverse(result.getBanks()));

            log.info("banks response body {}", result);
            return ResponseEntity.ok(result);
        } catch (IOException ex) {
            log.info("customer response :{}", ex.getLocalizedMessage());
            return null;
        }
    }
}
