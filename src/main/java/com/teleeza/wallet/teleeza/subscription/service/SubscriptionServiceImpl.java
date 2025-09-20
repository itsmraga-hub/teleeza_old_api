package com.teleeza.wallet.teleeza.subscription.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.subscription.dtos.requests.SubscriptionDto;
import com.teleeza.wallet.teleeza.subscription.dtos.responses.SubscriptionResponse;
import com.teleeza.wallet.teleeza.subscription.entity.SubscriptionEntity;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionRepository;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository repository;
    private final SasaPayApi sasaPayApi;
    private final SasaPayConfig sasaPayConfig;
    private final ObjectMapper objectMapper;
    private final CustomerRegistrationRepository customerRegistrationRepository;


    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository repository,
                                   SasaPayApi sasaPayApi,
                                   SasaPayConfig sasaPayConfig,
                                   ObjectMapper objectMapper,
                                   CustomerRegistrationRepository customerRegistrationRepository) {
        this.repository = repository;
        this.sasaPayApi = sasaPayApi;
        this.sasaPayConfig = sasaPayConfig;
        this.objectMapper = objectMapper;
        this.customerRegistrationRepository = customerRegistrationRepository;
    }

    @Override
    public boolean existsById(Long subscriptionId) {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription id must not be null");
        }
        return repository.existsById(subscriptionId);
    }

    @Override
    public SubscriptionEntity findById(Long subscriptionId) {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID must not be NULL");
        }
        return repository.findById(subscriptionId).orElse(null);
    }

    @Override
    public SubscriptionEntity create(SubscriptionEntity subscriptionEntity) {
        if (subscriptionEntity == null) {
            throw new IllegalArgumentException("Subscription must not be NULL");
        }
        if (subscriptionEntity.getId() != null) {
            throw new IllegalArgumentException("Subscription ID must be NULL");
        }
        return repository.save(subscriptionEntity);
    }

    @Override
    public SubscriptionEntity update(SubscriptionEntity subscriptionEntity) {
        if (subscriptionEntity == null) {
            throw new IllegalArgumentException("Subscription must not be NULL");
        }
        if (subscriptionEntity.getId() == null) {
            throw new IllegalArgumentException("Subscription ID must not be NULL");
        }
        return repository.save(subscriptionEntity);
    }

    @Override
    public void deleteById(Long subscriptionId) {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID must not be NULL");
        }
        repository.deleteById(subscriptionId);
    }

    @Override
    public Page<SubscriptionEntity> findAll(Pageable pageable) {
        return null;
    }


    public String extendSubscription(SubscriptionDto internalSubscriptionRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders payBillsHeaders = new HttpHeaders();
        payBillsHeaders.setContentType(MediaType.APPLICATION_JSON);
        payBillsHeaders.setBearerAuth(sasaPayApi.getAccessToken().getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BillRefNumber", HelperUtility.getBillRefNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", internalSubscriptionRequest.getBeneficiaryAccNo());
        merchantToBeneficiary.put("SasaPayBillNumber", internalSubscriptionRequest.getPayBillNo());
        merchantToBeneficiary.put("Amount", internalSubscriptionRequest.getAmount());
        merchantToBeneficiary.put("MerchantCode", "669994");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("Reason", "Pay Bill");
        merchantToBeneficiary.put("CallBackUrl", "https://wallet.teleeza.africa/v1/teleeza-wallet/subscription-extension-validation");

        HttpEntity<Map<String, Object>> commission = new HttpEntity<>(merchantToBeneficiary, payBillsHeaders);
        ResponseEntity<SubscriptionResponse> commissionResponse = restTemplate.postForEntity(
                sasaPayConfig.getBillPaymentEndpoint(),
                commission, SubscriptionResponse.class);

        if (commissionResponse.getStatusCode() == HttpStatus.CREATED || commissionResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful");
            // find get user from the database, use the refferedbyCode to identity their referee and credit them
            System.out.println(commissionResponse.getBody());
            // TODO: save request and response in `all_transactions` table
        } else {
            // TODO: save request and response in `all_transactions` table
            System.out.println("Request Failed");
            System.out.println(commissionResponse.getStatusCode());
        }
        return "Subscription extended";
    }
}
