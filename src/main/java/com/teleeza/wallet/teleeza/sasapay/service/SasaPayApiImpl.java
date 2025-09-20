package com.teleeza.wallet.teleeza.sasapay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.authentication.dtos.responses.AccessTokenResponse;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.requests.BillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.requests.InternalBillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.responses.BillPaymentResponse;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.entity.BillsEntity;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.repository.BillPaymentRepository;
import com.teleeza.wallet.teleeza.sasapay.cashout.dtos.requests.CashoutRequest;
import com.teleeza.wallet.teleeza.sasapay.cashout.dtos.requests.InternalCashoutRequest;
import com.teleeza.wallet.teleeza.sasapay.cashout.dtos.responses.CashoutResponse;
import com.teleeza.wallet.teleeza.common.AcknowledgeResponse;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests.CustomerConfirmationRequest;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests.CustomerRegistrationRequest;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests.KycUpdateRequest;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.CustomerConfirmationResponse;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.CustomerRegistrationResponse;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.KycUpdateResponse;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests.InternalLoadCustomerWalletRequest;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests.TopUpAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests.TopUpVerificationRequest;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.responses.LoadCustomerWalletResponse;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.responses.TopupVerification;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.entities.LoadWalletEntity;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.repository.LoadWalletRepository;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.BankTransferRepository;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests.BankTransferAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests.BankTransferRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests.InternalBankTransfer;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.response.BankTransferResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.entity.BankTransferEntity;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests.InternalMobileMoneyTransferRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests.MobileMoneyTransferAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests.MobileMoneyTransferRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.responses.CustomerToMobileMoneyResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.request.CustomerToCustomerAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.request.InternalCustomerToCustomerRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.response.CustomerToCustomerResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.repository.CustomerToCustomerTransactionRepository;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests.InternalTillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests.TillPaymentAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests.TillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.responses.TillPaymentResponse;
import com.teleeza.wallet.teleeza.sasapay.till_payment.entity.TillsEntity;
import com.teleeza.wallet.teleeza.sasapay.till_payment.repository.TillsTransactionRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TransactionsEntity;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.AllTransactionsRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import com.teleeza.wallet.teleeza.utils.RestTemplateResponseErrorHandler;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.teleeza.wallet.teleeza.utils.Constants.*;

@Service
@Component
@Slf4j
@Transactional
public class SasaPayApiImpl implements SasaPayApi {

    private final SasaPayConfig sasaPayConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    @Autowired
    private AllTransactionsRepository allTransactionsRepository;
    @Autowired
    private TillsTransactionRepository tillsTransactionRepository;
    @Autowired
    private BillPaymentRepository billPaymentRepository;
    @Autowired
    private BankTransferRepository bankTransferRepository;
    @Autowired
    private CustomerToCustomerTransactionRepository customerToCustomerTransactionRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;

    @Autowired
    private LoadWalletRepository loadWalletRepository;

    public SasaPayApiImpl(SasaPayConfig sasaPayConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.sasaPayConfig = sasaPayConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;

    }

    @Override
    public AccessTokenResponse getAccessToken() {
        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s",
                sasaPayConfig.getClientID(), sasaPayConfig.getClientSecret()));

        String url = String.format("%s?grant_type=%s", sasaPayConfig.getOauthEndPoint(), sasaPayConfig.getGrantType());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(sasaPayConfig.getClientID(), sasaPayConfig.getClientSecret());

        // build the request
        HttpEntity request = new HttpEntity(httpHeaders);

        // make an HTTP GET request with headers
        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                AccessTokenResponse.class

        );

        // check response
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return response.getBody();
        }

//        log.info("=====BASIC TOKEN=====");
//        log.info(String.format("BEARER %s", encodedCredentials));
//
//        Request request = new Request.Builder()
//                .url(String.format("%s?grant_type=%s", sasaPayConfig.getOauthEndPoint(), sasaPayConfig.getGrantType()))
//                .get()
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
//                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
//                .build();
//
//
//        log.info(" Auth request{}", request);
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            assert response.body() != null;
//            // Deserialize response body to Java object
//            return objectMapper.readValue(response.body().string(), AccessTokenResponse.class);
//        } catch (IOException e) {
//            log.error("Could not get access token. {}", e.getLocalizedMessage());
//            return null;
//        }

    }

    @Override
    public KycUpdateResponse updateUserKyc(KycUpdateRequest kycUpdateRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());

        Map<String, Object> updateUserKyc = new HashMap<>();
        updateUserKyc.put("MerchantCode", "669994");
        updateUserKyc.put("FirstName", kycUpdateRequest.getFirstName());
        updateUserKyc.put("MiddleName", kycUpdateRequest.getMiddleName());
        updateUserKyc.put("LastName", kycUpdateRequest.getLastName());
        updateUserKyc.put("DocumentType", kycUpdateRequest.getDocumentType());
        updateUserKyc.put("DocumentNumber", kycUpdateRequest.getDocumentNumber());
        updateUserKyc.put("Email", kycUpdateRequest.getEmail());
        updateUserKyc.put("BeneficiaryAccountNumber", kycUpdateRequest.getBeneficiaryAccountNumber());

        HttpEntity<Map<String, Object>> loadWalletRequest = new HttpEntity<>(updateUserKyc, httpHeaders);
        log.info("KYC update Request : {}", loadWalletRequest);
        ResponseEntity<KycUpdateResponse> kycUpdateResponse = restTemplate.postForEntity(
                sasaPayConfig.getKycUpdateEndpoint(),
                loadWalletRequest, KycUpdateResponse.class);

        log.info("Response Code : {}", kycUpdateResponse.getStatusCode());

        if (kycUpdateResponse.getStatusCode() == HttpStatus.OK) {

            CustomerEntity customer = customerRegistrationRepository.findByCustomerAccountNumber(kycUpdateRequest.getBeneficiaryAccountNumber());
            customer.setDocumentNumber(kycUpdateRequest.getDocumentNumber());
            customer.setDocumentType(kycUpdateRequest.getDocumentType());
            customerRegistrationRepository.save(customer);

            return kycUpdateResponse.getBody();
        } else if (kycUpdateResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return kycUpdateResponse.getBody();
        } else {
            return kycUpdateResponse.getBody();
        }
    }

    @Override
    @Transactional
    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(customerRegistrationRequest)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getBeneficiaryOnBoardingEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), CustomerRegistrationResponse.class);
        } catch (IOException ex) {
            log.error("Could not register customer {}", ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public CustomerConfirmationResponse customerRegistrationConfirmation(
            CustomerConfirmationRequest customerConfirmationRequest) {

        AccessTokenResponse accessTokenResponse = getAccessToken();
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(customerConfirmationRequest)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getCustomerRegistrationConfirmationEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), CustomerConfirmationResponse.class);
        } catch (IOException ex) {
            log.error("Unable to confirm customer {}", ex.getLocalizedMessage());
            return null;
        }
    }

    // Customer loads their wallet
    @Override
    @Transactional
    public LoadCustomerWalletResponse merchantLoadCustomerWallet(
            InternalLoadCustomerWalletRequest internalLoadCustomerWalletRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("NetworkCode", internalLoadCustomerWalletRequest.getNetworkCode());
        merchantToBeneficiary.put("MobileNumber", internalLoadCustomerWalletRequest.getMobileNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
        merchantToBeneficiary.put("Amount", internalLoadCustomerWalletRequest.getAmount());
        merchantToBeneficiary.put("MerchantCode", "669994");
        merchantToBeneficiary.put("CurrencyCode", "KES");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getLoadWalletCallBack());
        merchantToBeneficiary.put("Reason", "Load Customer Wallet");

        HttpEntity<Map<String, Object>> loadWalletRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        ResponseEntity<LoadCustomerWalletResponse> loadWalletResponse = restTemplate.postForEntity(
                sasaPayConfig.getLoadCustomerWalletEndpoint(),
                loadWalletRequest, LoadCustomerWalletResponse.class);

        Transactions transactions = new Transactions();
        LoadWalletEntity loadWallet = new LoadWalletEntity();
        if (loadWalletResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful");
            transactions.setMerchantRequestId(loadWalletResponse.getBody().getMerchantReference());

            if (internalLoadCustomerWalletRequest.getNetworkCode().equals("63902")) {
//                transactions.setMerchantRequestId(loadWalletResponse.getBody().getMerchantReference());
//                transactions.setBeneficiaryAccNumber(internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
//                transactions.setReason("Top Up: M-Pesa");
//                transactions.setSourceChannel("M-Pesa");
//                log.info("TRANSACTION SAVED");
//                transactionsRepository.save(transactions);

                loadWallet.setAmount(internalLoadCustomerWalletRequest.getAmount());
                loadWallet.setBeneficiaryAccountNumber(internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
                loadWallet.setMobileNumber(internalLoadCustomerWalletRequest.getMobileNumber());
                loadWallet.setNetworkCode(internalLoadCustomerWalletRequest.getNetworkCode());
                loadWallet.setMerchantCode("669994");
                loadWallet.setCurrency("KES");
                loadWallet.setTransactionFee("0");
                loadWallet.setReason("Top Up: M-Pesa");
                loadWallet.setSourceChannel("M-Pesa");
                loadWallet.setMerchantReference(loadWalletResponse.getBody().getMerchantReference());
                loadWallet.setStatusCode(loadWalletResponse.getBody().getStatusCode());
                loadWallet.setTransactionRef(loadWalletResponse.getBody().getTransactionReference());

                loadWalletRepository.save(loadWallet);

            }
            if (internalLoadCustomerWalletRequest.getNetworkCode().equals("63903")) {
//                transactions.setMerchantRequestId(loadWalletResponse.getBody().getMerchantReference());
//                transactions.setBeneficiaryAccNumber(internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
//                transactions.setReason("Top Up: AirtelMoney");
//                transactions.setSourceChannel("AirtelMoney");
//                transactionsRepository.save(transactions);

                loadWallet.setAmount(internalLoadCustomerWalletRequest.getAmount());
                loadWallet.setBeneficiaryAccountNumber(internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
                loadWallet.setMobileNumber(internalLoadCustomerWalletRequest.getMobileNumber());
                loadWallet.setNetworkCode(internalLoadCustomerWalletRequest.getNetworkCode());
                loadWallet.setMerchantCode("669994");
                loadWallet.setCurrency("KES");
                loadWallet.setTransactionFee("0");
                loadWallet.setReason("Top Up: AirtelMoney");
                loadWallet.setSourceChannel("AirtelMoney");
                loadWallet.setMerchantReference(loadWalletResponse.getBody().getMerchantReference());
                loadWallet.setStatusCode(loadWalletResponse.getBody().getStatusCode());
                loadWallet.setTransactionRef(loadWalletResponse.getBody().getTransactionReference());
                loadWalletRepository.save(loadWallet);
            }
            if (internalLoadCustomerWalletRequest.getNetworkCode().equals("63907")) {
//                transactions.setMerchantRequestId(loadWalletResponse.getBody().getMerchantReference());
//                transactions.setBeneficiaryAccNumber(internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
//                transactions.setReason("Top Up: T-Kash");
//                transactions.setSourceChannel("T-Kash");
//                transactionsRepository.save(transactions);


                loadWallet.setAmount(internalLoadCustomerWalletRequest.getAmount());
                loadWallet.setBeneficiaryAccountNumber(internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
                loadWallet.setMobileNumber(internalLoadCustomerWalletRequest.getMobileNumber());
                loadWallet.setNetworkCode(internalLoadCustomerWalletRequest.getNetworkCode());
                loadWallet.setMerchantCode("669994");
                loadWallet.setCurrency("KES");
                loadWallet.setTransactionFee("0");
                loadWallet.setReason("Top Up: T-Kash");
                loadWallet.setSourceChannel("T-Kash");
                loadWallet.setMerchantReference(loadWalletResponse.getBody().getMerchantReference());
                loadWallet.setStatusCode(loadWalletResponse.getBody().getStatusCode());
                loadWallet.setTransactionRef(loadWalletResponse.getBody().getTransactionReference());
                loadWalletRepository.save(loadWallet);
            }

            if (internalLoadCustomerWalletRequest.getNetworkCode().equals("63909")) {
//                transactions.setMerchantRequestId(loadWalletResponse.getBody().getMerchantReference());
//                transactions.setBeneficiaryAccNumber(internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
//                transactions.setReason("Top Up: SasaPay");
//                transactions.setSourceChannel("SasaPay");
//                transactionsRepository.save(transactions);

                loadWallet.setAmount(internalLoadCustomerWalletRequest.getAmount());
                loadWallet.setBeneficiaryAccountNumber(internalLoadCustomerWalletRequest.getBeneficiaryAccountNumber());
                loadWallet.setMobileNumber(internalLoadCustomerWalletRequest.getMobileNumber());
                loadWallet.setNetworkCode(internalLoadCustomerWalletRequest.getNetworkCode());
                loadWallet.setMerchantCode("669994");
                loadWallet.setCurrency("KES");
                loadWallet.setTransactionFee("0");
                loadWallet.setReason("Top Up: SasaPay");
                loadWallet.setSourceChannel("SasaPay");
                loadWallet.setMerchantReference(loadWalletResponse.getBody().getMerchantReference());
                loadWallet.setStatusCode(loadWalletResponse.getBody().getStatusCode());
                loadWallet.setTransactionRef(loadWalletResponse.getBody().getTransactionReference());
                loadWalletRepository.save(loadWallet);
            }
            System.out.println(loadWalletResponse.getBody());
            return loadWalletResponse.getBody();
        } else {
            return loadWalletResponse.getBody();
        }
    }

    // Send money from a Teleeza account to another Teleeza account under teleeza
    @Override
    @Transactional
    public CustomerToCustomerResponse customerToCustomerSendMoney(
            InternalCustomerToCustomerRequest internalCustomerToCustomerRequest
    ) {
        String mobile = "+254" + internalCustomerToCustomerRequest.getRecipientPhoneNumber().substring(1);

        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(mobile);

        AccessTokenResponse accessTokenResponse = getAccessToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("SenderBeneficiaryAccountNumber", internalCustomerToCustomerRequest.getSenderBeneficiaryAccountNumber());
        merchantToBeneficiary.put("RecipientBeneficiaryAccountNumber", customer.getCustomerAccountNumber());
        merchantToBeneficiary.put("Amount", internalCustomerToCustomerRequest.getAmount());
        merchantToBeneficiary.put("MerchantCode", "669994");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getC2cCallBack());

        HttpEntity<Map<String, Object>> customerToCustomerRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);


        // Send Request to SasaPay's sever
        ResponseEntity<CustomerToCustomerResponse> c2cResponse = restTemplate.postForEntity(
                sasaPayConfig.getCustomerToCustomerTransferEndpoint(),
                customerToCustomerRequest, CustomerToCustomerResponse.class);
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

        // Save transaction request and response to all_transactions `attempts table`
//        CustomerToCustomerEntity customerToCustomerEntity = new CustomerToCustomerEntity();
//        customerToCustomerEntity.setSenderBeneficiaryAccountNumber(internalCustomerToCustomerRequest.getSenderBeneficiaryAccountNumber());
//        customerToCustomerEntity.setRecipientBeneficiaryAccountNumber(customer.getCustomerAccountNumber());
//        customerToCustomerEntity.setAmount(internalCustomerToCustomerRequest.getAmount());
//        customerToCustomerEntity.setMerchantCode("669994");
//        customerToCustomerEntity.setTransactionReference(c2cResponse.getBody().getTransactionReference());
//        customerToCustomerEntity.setMerchantReference(c2cResponse.getBody().getMerchantReference());
//        customerToCustomerEntity.setMerchantRequestId(c2cResponse.getBody().getMerchantReference());
//        customerToCustomerEntity.setStatusCode(c2cResponse.getBody().getStatusCode());
//        customerToCustomerEntity.setResultDescription(c2cResponse.getBody().getMessage());
//        customerToCustomerTransactionRepository.save(customerToCustomerEntity);

//        if (c2cResponse.getStatusCode() == HttpStatus.OK) {
//            System.out.println("Request Successful");
//
//
//        } else if (c2cResponse.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
//            return c2cResponse.getBody();
//        } else {
//            return c2cResponse.getBody();
//        }

        // Await response from the callback url
        ResponseEntity<CustomerToCustomerAsyncRequest> callbackResponse = null;
        try {
            callbackResponse = restTemplate.postForEntity(
                    sasaPayConfig.getCallBackUrl(),
                    c2cResponse.getBody(),
                    CustomerToCustomerAsyncRequest.class);
        }catch (RestClientException exception){
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            log.info("Transaction Failed => No Response from callback : {}", c2cResponse.getBody());
            CustomerToCustomerAsyncRequest responseBody = callbackResponse.getBody();
        }

        if(callbackResponse.getStatusCode().is2xxSuccessful()){
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            CustomerToCustomerAsyncRequest responseBody = callbackResponse.getBody();
            if (c2cResponse.getStatusCode() == HttpStatus.OK) {
                System.out.println("Request Successful");


            } else if (c2cResponse.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                return c2cResponse.getBody();
            } else {
                return c2cResponse.getBody();
            }
//            if (Objects.requireNonNull(c2cResponse.getBody()).getStatusCode().equals("0")) {
//                log.info("Response : {}", c2cResponse.getBody());
//                return c2cResponse.getBody();
//            } else if (c2cResponse.getBody().getStatusCode().equals("422")) {
//                log.info("Response : {}", "Insufficient balance");
//                return c2cResponse.getBody();
//            } else {
//                log.info("Response : {}", "An error occured. Bad Request");
//                return c2cResponse.getBody();
//            }
        }else {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            String responseBody = "{\"message\": \"Transaction failed: Callback returned an error\"}";
            return c2cResponse.getBody();
        }

        return c2cResponse.getBody();
    }

    // send money from Teleeza account to mobile money providers (Mpesa,Tkash,Airtel Money)
    @Override
    @Transactional
    public CustomerToMobileMoneyResponse customerToMobileMoney(InternalMobileMoneyTransferRequest internalMobileMoneyTransferRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();
        MobileMoneyTransferRequest mobileMoneyTransferRequest = new MobileMoneyTransferRequest();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        Map<String, Object> customerToMobileMoney = new HashMap<>();
        customerToMobileMoney.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        customerToMobileMoney.put("BeneficiaryAccountNumber", internalMobileMoneyTransferRequest.getBeneficiaryAccountNumber());
        customerToMobileMoney.put("MobileOperatorNumber", internalMobileMoneyTransferRequest.getMobileOperatorNumber());
        customerToMobileMoney.put("ChannelCode", internalMobileMoneyTransferRequest.getChannelCode());
        customerToMobileMoney.put("Amount", internalMobileMoneyTransferRequest.getAmount());
        customerToMobileMoney.put("MerchantCode", "669994");
        customerToMobileMoney.put("TransactionFee", 0);
        customerToMobileMoney.put("Reason", 0);
        customerToMobileMoney.put("CallBackUrl", sasaPayConfig.getMobileTransferCallback());

        HttpEntity<Map<String, Object>> customerToMobileMoneyRequest = new HttpEntity<>(customerToMobileMoney, httpHeaders);
        ResponseEntity<CustomerToMobileMoneyResponse> customerToMobileMoneyResponse = restTemplate.postForEntity(
                sasaPayConfig.getCustomerToMobileMoneyEndpoint(),
                customerToMobileMoneyRequest, CustomerToMobileMoneyResponse.class);

        if (customerToMobileMoneyResponse.getStatusCode() == HttpStatus.OK) {
            log.info("Request Successful {}", customerToMobileMoneyResponse.getBody().toString());
            return customerToMobileMoneyResponse.getBody();
        } else {
            log.info("Request Failed {}", customerToMobileMoneyResponse.getBody());
            return customerToMobileMoneyResponse.getBody();
        }
    }


    // Pay bills to SasaPay registered paybills
    @Override
    @Transactional
    public BillPaymentResponse payBills(InternalBillPaymentRequest internalBillPaymentRequest) {

        AccessTokenResponse accessTokenResponse = getAccessToken();
        BillPaymentRequest request = new BillPaymentRequest();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", internalBillPaymentRequest.getBeneficiaryAccountNumber());
        merchantToBeneficiary.put("SasaPayBillNumber", internalBillPaymentRequest.getSasaPayBillNumber());
        merchantToBeneficiary.put("Amount", internalBillPaymentRequest.getAmount());
        merchantToBeneficiary.put("MerchantCode", "669994");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("Reason", "Pay Bill");
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getPayBillsCallBack());

        HttpEntity<Map<String, Object>> billPaymentRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        ResponseEntity<BillPaymentResponse> billPaymentResponse = restTemplate.postForEntity(
                sasaPayConfig.getBillPaymentEndpoint(),
                billPaymentRequest, BillPaymentResponse.class);

        if (billPaymentResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Paybill Request Successful");

            BillsEntity billsEntity = new BillsEntity();
            billsEntity.setPayBillNumber(String.valueOf(internalBillPaymentRequest.getSasaPayBillNumber()));
            billsEntity.setBeneficiaryAccountNumber(internalBillPaymentRequest.getBeneficiaryAccountNumber());
            billsEntity.setMerchantCode(sasaPayConfig.getMerchantCode());
            billsEntity.setAmount(String.valueOf(internalBillPaymentRequest.getAmount()));
            billsEntity.setBillRefNumber(billPaymentResponse.getBody().getMerchantReference());
            billsEntity.setTransactionReference(billPaymentResponse.getBody().getMerchantReference());
            billsEntity.setReferenceNumber(billPaymentResponse.getBody().getReferenceNumber());
            billsEntity.setStatusCode(billPaymentResponse.getBody().getStatusCode());
            billsEntity.setReason("PayBill");
            billsEntity.setMessage(billPaymentResponse.getBody().getMessage());
            billPaymentRepository.save(billsEntity);

            return billPaymentResponse.getBody();
        } else {
            return billPaymentResponse.getBody();
        }
    }

    @Override
    @Transactional
    public TillPaymentResponse payToTills(InternalTillPaymentRequest internalTillPaymentRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();
        TillPaymentRequest tillPaymentRequest = new TillPaymentRequest();
        tillPaymentRequest.setTransactionReference(HelperUtility.getTransactionUniqueNumber());
        tillPaymentRequest.setBeneficiaryAccountNumber(internalTillPaymentRequest.getBeneficiaryAccountNumber());
        tillPaymentRequest.setSasaPayBillNumber(internalTillPaymentRequest.getSasaPayBillNumber());
        tillPaymentRequest.setAmount(internalTillPaymentRequest.getAmount());
        tillPaymentRequest.setMerchantCode(sasaPayConfig.getMerchantCode());
        tillPaymentRequest.setTransactionFee(0);
        tillPaymentRequest.setReason("Pay Till");
        tillPaymentRequest.setCallBackUrl(sasaPayConfig.getLipaTillsCallback());

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(tillPaymentRequest)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getLipaTillEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            TillPaymentResponse tillResponse = objectMapper.readValue(response.body().string(), TillPaymentResponse.class);
            if (response.isSuccessful()) {
                // update on tills tables
                TillsEntity tillsEntity = new TillsEntity();
                tillsEntity.setSasaPayBillNumber(String.valueOf(internalTillPaymentRequest.getSasaPayBillNumber()));
                tillsEntity.setTransactionAmount(String.valueOf(internalTillPaymentRequest.getAmount()));
                tillsEntity.setMerchantCode("669994");
                tillsEntity.setBeneficiaryAccountNumber(internalTillPaymentRequest.getBeneficiaryAccountNumber());
                tillsEntity.setStatusCode(tillResponse.getStatusCode());
                tillsEntity.setMerchantReference(tillResponse.getMerchantReference());
                tillsEntity.setReferenceNumber(tillResponse.getReferenceNumber());
                tillsEntity.setResultDescription(tillResponse.getMessage());
                tillsTransactionRepository.save(tillsEntity);
                return tillResponse;
            }
            //Deserialize to Java object;
            return tillResponse;

        } catch (IOException ex) {
            log.error("Unable to pay to till {}", ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public CashoutResponse withDrawFromAgent(InternalCashoutRequest internalCashoutRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();

        CashoutRequest cashoutRequest = new CashoutRequest();
        cashoutRequest.setTransactionReference(HelperUtility.getTransactionUniqueNumber());
        cashoutRequest.setBeneficiaryAccountNumber(internalCashoutRequest.getBeneficiaryAccountNumber());
        cashoutRequest.setSasaPayAgentNumber(internalCashoutRequest.getSasaPayAgentNumber());
        cashoutRequest.setAmount(internalCashoutRequest.getAmount());
        cashoutRequest.setMerchantCode(sasaPayConfig.getMerchantCode());
        cashoutRequest.setTransactionFee(0);
        cashoutRequest.setCallBackUrl(sasaPayConfig.getCashoutValidationCallBack());
        cashoutRequest.setReason("Agent withdrawal");

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(cashoutRequest)
        ));

        Request request = new Request.Builder()
                .url(sasaPayConfig.getCashoutEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), CashoutResponse.class);
        } catch (IOException ex) {
            log.error("Unable to  withdraw from an agent {}", ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public BankTransferResponse sendToBank(InternalBankTransfer internalBankTransfer) {

        AccessTokenResponse accessTokenResponse = getAccessToken();
        BankTransferRequest bankTransferRequest = new BankTransferRequest();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessTokenResponse.getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", internalBankTransfer.getBeneficiaryAccountNumber());
        merchantToBeneficiary.put("ReceiverNumber", internalBankTransfer.getReceiverNumber());
        merchantToBeneficiary.put("ChannelCode", internalBankTransfer.getChannelCode());
        merchantToBeneficiary.put("Amount", internalBankTransfer.getAmount());
        merchantToBeneficiary.put("MerchantCode", "669994");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("Reason", internalBankTransfer.getReason());
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getBankTransferValidation());

        HttpEntity<Map<String, Object>> customerToBankRequest = new HttpEntity<>(merchantToBeneficiary, httpHeaders);
        ResponseEntity<BankTransferResponse> customerToBankResponse = restTemplate.postForEntity(
                sasaPayConfig.getBankTransferEndpoint(),
                customerToBankRequest, BankTransferResponse.class);


        if (customerToBankResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful");
            // save transaction attempt
            BankTransferEntity bankTransfer = new BankTransferEntity();
            bankTransfer.setBeneficiaryAccountNumber(internalBankTransfer.getBeneficiaryAccountNumber());
            bankTransfer.setReceiverNumber(internalBankTransfer.getReceiverNumber());
            bankTransfer.setChannelCode(internalBankTransfer.getChannelCode());
            bankTransfer.setReason(internalBankTransfer.getReason());
            bankTransfer.setAmount(internalBankTransfer.getAmount());
            bankTransfer.setTransactionReference(customerToBankResponse.getBody().getTransactionReference());
            bankTransfer.setStatusCode(customerToBankResponse.getBody().getStatusCode());
            bankTransfer.setReferenceNumber(customerToBankResponse.getBody().getReferenceNumber());
            bankTransfer.setResultDescription(customerToBankResponse.getBody().getMessage());
            bankTransferRepository.save(bankTransfer);
            // Save transaction request and response to all_transactions `attempts table`
            TransactionsEntity transactionsEntity = new TransactionsEntity();
            transactionsEntity.setSenderBeneficiaryAccNumber(bankTransferRequest.getBeneficiaryAccountNumber());
            transactionsEntity.setAmount(Double.valueOf(internalBankTransfer.getAmount()));
            transactionsEntity.setMerchantCode("669994");
            transactionsEntity.setResultCode(customerToBankResponse.getBody().getStatusCode());
            transactionsEntity.setResultDesc(customerToBankResponse.getBody().getMessage());
            transactionsEntity.setTransactionFee(0);
            transactionsEntity.setReason("Bank Transfer");
            transactionsEntity.setRecipientBeneficiaryAccNumber(internalBankTransfer.getReceiverNumber());
            transactionsEntity.setMerchantTransactionRef(Objects.requireNonNull(customerToBankResponse.getBody()).getTransactionReference());
            transactionsEntity.setTransactionReference(customerToBankResponse.getBody().getTransactionReference());
            allTransactionsRepository.save(transactionsEntity);
            System.out.println(customerToBankResponse.getBody());
            return customerToBankResponse.getBody();

        } else {
            return customerToBankResponse.getBody();
        }
    }

    @Override
    public TopupVerification verifyTopUpWallet(TopUpVerificationRequest topUpVerificationRequest) {
        RequestBody body = new FormBody.Builder()
                .add("MerchantRequestID", topUpVerificationRequest.getMerchantRequestID())
                .add("BillRefNumber", topUpVerificationRequest.getBillRefNumber())
                .build();

        Request request = new Request.Builder()
                .url(sasaPayConfig.getTopupVerification())
                .post(body)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            return objectMapper.readValue(response.body().string(), TopupVerification.class);
        } catch (IOException ex) {
            log.error("verification response {}", ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public AcknowledgeResponse validateLoadWallet(TopUpAsyncRequest topUpAsyncRequest) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(topUpAsyncRequest)
        ));
        Request request = new Request.Builder()
                .url(sasaPayConfig.getWalletCallBack())
                .post(body)
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
//                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), AcknowledgeResponse.class);
        } catch (IOException ex) {
            log.error("top up validation error response {}", ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public AcknowledgeResponse validateMobileTransfer(MobileMoneyTransferAsyncRequest mobileMoneyTransferAsyncRequest) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(mobileMoneyTransferAsyncRequest)
        ));
        Request request = new Request.Builder()
                .url(sasaPayConfig.getWalletCallBack())
                .post(body)
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
//                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), AcknowledgeResponse.class);
        } catch (IOException ex) {
            log.error("top up validation error response {}", ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public AcknowledgeResponse validateBankTransferTransactions(BankTransferAsyncRequest bankTransferAsyncRequest) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(bankTransferAsyncRequest)
        ));
        Request request = new Request.Builder()
                .url(sasaPayConfig.getWalletCallBack())
                .post(body)
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
//                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), AcknowledgeResponse.class);
        } catch (IOException ex) {
            log.error("top up validation error response {}", ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public AcknowledgeResponse validateC2CTransaction(CustomerToCustomerAsyncRequest customerToCustomerAsyncRequest) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(customerToCustomerAsyncRequest)
        ));
        Request request = new Request.Builder()
                .url(sasaPayConfig.getWalletCallBack())
                .post(body)
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
//                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), AcknowledgeResponse.class);
        } catch (IOException ex) {
            log.error("top up validation error response {}", ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public AcknowledgeResponse validateTillTransactions(TillPaymentAsyncRequest tillPaymentAsyncRequest) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(
                HelperUtility.toJson(tillPaymentAsyncRequest)
        ));
        Request request = new Request.Builder()
                .url(sasaPayConfig.getWalletCallBack())
                .post(body)
//                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
//                        BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), AcknowledgeResponse.class);
        } catch (IOException ex) {
            log.error("top up validation error response {}", ex.getLocalizedMessage());
            return null;
        }
    }
}
