package com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.common.config.KokotoaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.notification.model.dtos.requests.PushNotificationRequest;
import com.teleeza.wallet.teleeza.notification.service.PushNotificationService;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.request.CustomerToCustomerAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.request.InternalCustomerToCustomerRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.response.CustomerToCustomerResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.service.C2CService;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import com.teleeza.wallet.teleeza.utils.RestTemplateResponseErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;


@RestController
@RequestMapping("/v1/teleeza-wallet")
@Slf4j
public class CustomerToCustomerTransferController {
    private final SasaPayApi sasapayApi;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final ObjectMapper objectMapper;
    private final TransactionsRepository transactionsRepository;
    private final KokotoaConfig kokotoaConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired private C2CService c2CService;
    @Autowired
    private PushNotificationService pushNotificationService;
    Logger logger = LoggerFactory.getLogger(CustomerToCustomerTransferController.class);

    public CustomerToCustomerTransferController(SasaPayApi sasapayApi,
                                                CustomerRegistrationRepository customerRegistrationRepository,
                                                ObjectMapper objectMapper,
                                                TransactionsRepository transactionsRepository, KokotoaConfig kokotoaConfig) {
        this.sasapayApi = sasapayApi;
        this.customerRegistrationRepository = customerRegistrationRepository;
        this.objectMapper = objectMapper;
        this.transactionsRepository = transactionsRepository;
        this.kokotoaConfig = kokotoaConfig;
    }



    @PostMapping(path = "/customer-to-customer", produces = "application/json")
    public ResponseEntity<CustomerToCustomerResponse> performCustomerToCustomerTransfer(
            @RequestBody InternalCustomerToCustomerRequest internalCustomerToCustomerRequest)
            throws JsonProcessingException {
        String mobile = "+254" + internalCustomerToCustomerRequest.getRecipientPhoneNumber().substring(1);
        Boolean existsByPhone = c2CService.existsByPhone(mobile);
        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(mobile);
        if(existsByPhone && Objects.nonNull(customer.getCustomerAccountNumber())){
            CustomerToCustomerResponse response = sasapayApi.customerToCustomerSendMoney(internalCustomerToCustomerRequest);
            String jsonresponse = objectMapper.writeValueAsString(response);
            CustomerToCustomerResponse transactionResponse = objectMapper.readValue(jsonresponse, CustomerToCustomerResponse.class);
            return ResponseEntity.ok(response);
        }else {
            CustomerToCustomerResponse response = new CustomerToCustomerResponse();
            response.setMerchantReference("");
            response.setTransactionReference("");
            response.setMessage("User with phone no "+internalCustomerToCustomerRequest.getRecipientPhoneNumber()+ " does not have a wallet account");
            response.setStatusCode("400");
            String jsonresponse = objectMapper.writeValueAsString(response);
            CustomerToCustomerResponse transactionResponse = objectMapper.readValue(jsonresponse, CustomerToCustomerResponse.class);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/c2c-validation")
    public ResponseEntity<CustomerToCustomerAsyncRequest> validateC2CTransaction(
            @RequestBody CustomerToCustomerAsyncRequest customerToCustomerAsyncRequest
    ) throws InterruptedException {
        // Sender
        String customerPhone = customerToCustomerAsyncRequest.getSenderCustomerMobile();
        String phone = "+" + customerPhone; // append `+` so that the number reads `+2547XXXX` or `+25411XXX`
        log.info("SENDER PHONE RESPONSE {}", phone);
        CustomerEntity entity =
                customerRegistrationRepository.findCustomerByPhoneNumber(phone);
        // Recipient
        String receiverPhone = customerToCustomerAsyncRequest.getReceiverCustomerMobile();
        String recipientPhone = "+" + receiverPhone; // append `+` so that the number reads `+2547XXXX` or `+25411XXX`

        CustomerEntity recipient = customerRegistrationRepository.findCustomerByPhoneNumber(recipientPhone);

        // Save sender's transactions
        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(customerToCustomerAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(customerToCustomerAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(customerToCustomerAsyncRequest.getResultCode());
        transactions.setResultDesc(customerToCustomerAsyncRequest.getResultDesc());
        transactions.setTransactionAmount(customerToCustomerAsyncRequest.getTransAmount());
        transactions.setBillRefNumber(customerToCustomerAsyncRequest.getBillRefNumber());
        transactions.setBeneficiaryAccNumber(entity.getCustomerAccountNumber());
        transactions.setTransactionDate(customerToCustomerAsyncRequest.getTransactionDate());
        transactions.setSenderAccountNumber(customerToCustomerAsyncRequest.getSenderAccountNumber());
        transactions.setRecipientAccountNumber(customerToCustomerAsyncRequest.getReceiverAccuntNumber());
        transactions.setSasaPayTransactionId("");
        transactions.setThirdPartyId("");
        transactions.setIsTransactionType(false);
        transactions.setReason("Send Money: C2C");
        transactions.setRecipientName(recipient.getDisplayName());
        transactions.setSenderName(entity.getDisplayName());
        transactions.setMerchantTransactionReference("");
        transactions.setSourceChannel("0");
        transactions.setTransactionFee("0");
        transactions.setDestinationChannel("0");
        transactions.setMerchantAccountBalance("");
        transactionsRepository.save(transactions);

        // send push notification to sender
        PushNotificationRequest notificationRequest = new PushNotificationRequest();
        notificationRequest.setTitle("Teleeza Wallet");
        notificationRequest.setMessage("Hi "+entity.getFirstName()+" , Confirmed You have sent Ksh "+customerToCustomerAsyncRequest.getTransAmount()+" to " +recipient.getDisplayName()+". Thank you for choosing Teleeza");
        notificationRequest.setToken(entity.getFcmToken());
        notificationRequest.setTopic("Send Money");
        pushNotificationService.sendPushNotificationToToken(notificationRequest);

        // send push notification to recipient
        PushNotificationRequest request = new PushNotificationRequest();
        request.setTitle("Teleeza Wallet");
        request.setMessage("Hi "+recipient.getFirstName()+" ,Confirmed  You have received Ksh "
                +customerToCustomerAsyncRequest.getTransAmount()+" from " +entity.getDisplayName() + " Ref Code "
                + customerToCustomerAsyncRequest.getMerchantRequestID()+ ". Thank you for choosing Teleeza.");
        request.setToken(recipient.getFcmToken());
        request.setTopic("Send Money");
        pushNotificationService.sendPushNotificationToRecipientToken(request);

        // update transaction record on the recipients end
        Transactions recipientTransactions = new Transactions();
        recipientTransactions.setMerchantRequestId(customerToCustomerAsyncRequest.getMerchantRequestID());
        recipientTransactions.setCheckoutRequestId(customerToCustomerAsyncRequest.getCheckoutRequestID());
        recipientTransactions.setResultCode(customerToCustomerAsyncRequest.getResultCode());
        recipientTransactions.setResultDesc(customerToCustomerAsyncRequest.getResultDesc());
        recipientTransactions.setTransactionAmount(customerToCustomerAsyncRequest.getTransAmount());
        recipientTransactions.setBillRefNumber(customerToCustomerAsyncRequest.getBillRefNumber());
        recipientTransactions.setBeneficiaryAccNumber(recipient.getCustomerAccountNumber());
        recipientTransactions.setTransactionDate(customerToCustomerAsyncRequest.getTransactionDate());
        recipientTransactions.setSenderAccountNumber(entity.getCustomerAccountNumber());
        recipientTransactions.setRecipientAccountNumber(recipient.getCustomerAccountNumber());//replace with receiver acc no.
        recipientTransactions.setSasaPayTransactionId("");
        recipientTransactions.setThirdPartyId("");
        recipientTransactions.setIsTransactionType(true);
        recipientTransactions.setRecipientName(recipient.getDisplayName());
        recipientTransactions.setSenderName(entity.getDisplayName());
        recipientTransactions.setReason("Wallet: C2C");
        recipientTransactions.setMerchantTransactionReference("");
        recipientTransactions.setSourceChannel("0");
        recipientTransactions.setTransactionFee("0");
        recipientTransactions.setDestinationChannel("0");
        recipientTransactions.setMerchantAccountBalance("");
        transactionsRepository.save(recipientTransactions);

        //update sender's  record to kokotoa
        Map<String, String> map = new HashMap<>();
        map.put("name", "Send Money: C2C");
        map.put("description", "Send money to Teleeza User " + recipient.getCustomerAccountNumber());//+internalCustomerToCustomerRequest.getRecipientBeneficiaryAccountNumber()
        map.put("amount", String.valueOf(customerToCustomerAsyncRequest.getTransAmount()));
        map.put("category", "Expense");
        map.put("date", String.valueOf(LocalDate.now()));
        map.put("mobile", customerToCustomerAsyncRequest.getSenderCustomerMobile());
        map.put("transactionFee","0");
        map.put("beneficiaryAccount", entity.getCustomerAccountNumber());

        ResponseEntity<Void> kokotoaResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                map,
                Void.class);
        if (kokotoaResponse.getStatusCode() == HttpStatus.OK || kokotoaResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", kokotoaResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", kokotoaResponse.getStatusCode());
        }

        // update recipient record om Kokotoa
        Map<String, String> recipientMap = new HashMap<>();
        recipientMap.put("name", "Wallet: C2C");
        recipientMap.put("description", "Receive money from Teleeza User ");//+internalCustomerToCustomerRequest.getRecipientBeneficiaryAccountNumber()
        recipientMap.put("amount", String.valueOf(customerToCustomerAsyncRequest.getTransAmount()));
        recipientMap.put("category", "Income");
        recipientMap.put("date", String.valueOf(LocalDate.now()));
        recipientMap.put("mobile", customerToCustomerAsyncRequest.getSenderCustomerMobile());
        recipientMap.put("transactionFee","0");
        recipientMap.put("beneficiaryAccount", recipient.getCustomerAccountNumber());

        ResponseEntity<Void> recipientResponse = restTemplate.postForEntity(
                kokotoaConfig.getKokotoaApiEndpoint(),
                recipientMap,
                Void.class);
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        if (recipientResponse.getStatusCode() == HttpStatus.OK || recipientResponse.getStatusCode() == HttpStatus.CREATED) {
            log.info("Request Successful {}", recipientResponse.getStatusCode());
        } else {
            log.info("Request Failed {}", recipientResponse.getStatusCode());
        }

        return new ResponseEntity<>(customerToCustomerAsyncRequest, HttpStatus.OK);
    }
}
