package com.teleeza.wallet.teleeza.async;

import com.teleeza.wallet.teleeza.sasapay.config.SasaPayConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.dtos.response.MerchantToBeneficiaryResponse;
import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.service.MerchantToBeneficiarryApi;
import com.teleeza.wallet.teleeza.subscription.entity.SubscriptionEntity;
import com.teleeza.wallet.teleeza.subscription.repository.SubscriptionRepository;
import com.teleeza.wallet.teleeza.subscription.service.FreemiumSubscriptionService;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MerchantToBeneficiaryService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private MerchantToBeneficiarryApi merchantToBeneficiarryApi;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SasaPayConfig sasaPayConfig;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;

    private static final Logger logger = LoggerFactory.getLogger(FreemiumSubscriptionService.class);

    //     This method runs in the background to check and credit beneficiary after every 30 seconds
//    @Scheduled(cron = "0/30 * * * * ?")
    public void creditBeneficiarry() {
        SubscriptionEntity subscription = subscriptionRepository.getUncreditedSubscriptionsLimitByOne();

//        if(subscription != null && subscription.getPlanName().equals("Test")) {
//            logger.info("Subscription Plan   :  {}", subscription.getPlanName());
//
//            CustomerEntity customer = customerRegistrationRepository.getUserByAccNo(subscription.getBeneficiaryAccountNumber());
//            logger.info("===SUBSCRIBED CUSTOMER DETAILS ===");
//            logger.info("Customer Details  : {}", customer.getCustomerAccountNumber());
//            logger.info("Full Names  : {}", customer.getDisplayName());
//            logger.info("First Time Subscription  :{}", customer.getIsInitialSubscription());
//            logger.info("Referred By Code  : {}", customer.getReferredByCode());
//
//            // if the user  subscribes without a referralCode
//            // they don't get commissions on subsequent subscriptions , unless they refer someone
//            if(subscription.getReferredByCode()==null){
//                logger.info("Subscription Body without referredByCode  :  {}",subscription);
//                // Give a 6 % commission to the beneficiary and set `isInitialSubscriptionStatus` to false
//                // Do nothing if their `isInitialSubscriptionStatus` is false
//                if(customer.getIsInitialSubscription()){
//                    logger.info("===Customer's Initial Subscription ===");
//                    logger.info("Initial subscription  :  {}",customer.getIsInitialSubscription());
////                    merchantToBeneficiary(customer.getCustomerAccountNumber(),1,customer.getCustomerAccountNumber());
//                }
//                if(!customer.getIsInitialSubscription()){
//                    subscriptionRepository.updateSubscriptionTable(subscription.getBeneficiaryAccountNumber());
//                }
//            }
//
//            // if the user  subscribes with a referralCode on initial subscription they get a 6% discount and their referrer gets a
//            // commission of Ksh 50.00
//            if(subscription.getReferredByCode()!=null){
//                logger.info("Subscription Body with referredByCode  :  {}",subscription);
//                if(customer.getIsInitialSubscription()){
//                    logger.info("===Customer's Initial Subscription ===");
//                    logger.info("Initial subscription  :  {}",customer.getIsInitialSubscription());
//                    logger.info("ReferredByCode  :  {}",customer.getReferredByCode());
//
//                    // Get the details of the referring customer
//                    CustomerEntity referringCustomer = customerRegistrationRepository.findUserByReferralCode(customer.getReferredByCode());
//                    logger.info("==== REFERRING CUSTOMER =====");
//                    logger.info("Full Names   :  {}", referringCustomer.getDisplayName());
//                    logger.info("Account Number  : {}", referringCustomer.getCustomerAccountNumber());
//
//                    // send Ksh 50 to the referring customer represented as 5 for test
////                    merchantToBeneficiary(referringCustomer.getCustomerAccountNumber(), 5, customer.getCustomerAccountNumber());
//
//                    // send Ksh 15 to the subscribing  customer represented as 1 for test
////                    merchantToBeneficiary(customer.getCustomerAccountNumber(), 1, customer.getCustomerAccountNumber());
//
//                }
//                if(!customer.getIsInitialSubscription()){
//                    logger.info("===Customer's Initial Subscription ===");
//                    logger.info("Initial subscription  :  {}",customer.getIsInitialSubscription());
//                    logger.info("ReferredByCode  :  {}",customer.getReferredByCode());
//
//                    // Get the details of the referring customer
//                    CustomerEntity referringCustomer = customerRegistrationRepository.findUserByReferralCode(customer.getReferredByCode());
//                    logger.info("==== REFERRING CUSTOMER =====");
//                    logger.info("Full Names   :  {}", referringCustomer.getDisplayName());
//                    logger.info("Account Number  : {}", referringCustomer.getCustomerAccountNumber());
//
////                    merchantToBeneficiary(referringCustomer.getCustomerAccountNumber(),1,customer.getCustomerAccountNumber());
//                }
//            }
//        }

        if (subscription != null && subscription.getPlanName().equals("Monthly")) {
            CustomerEntity customer = customerRegistrationRepository.getUserByAccNo(subscription.getBeneficiaryAccountNumber());
            // if the user  subscribes without a referralCode
            // they don't get commissions on subsequent subscriptions , unless they refer someone
            if (subscription.getReferredByCode() == null) {
                // Give a 6 % commission to the beneficiary and set `isInitialSubscriptionStatus` to false
                // Do nothing if their `isInitialSubscriptionStatus` is false
                if (customer.getIsInitialSubscription()) {
                    merchantToBeneficiary(customer.getCustomerAccountNumber(), 1, customer.getCustomerAccountNumber());
                }
                if (!customer.getIsInitialSubscription()) {
                    subscriptionRepository.updateSubscriptionTable(subscription.getBeneficiaryAccountNumber());
                }
            }

            // if the user  subscribes with a referralCode on initial subscription they get a 6% discount and their referrer gets a
            // commission of Ksh 50.00
            if (subscription.getReferredByCode() != null) {
                if (customer.getIsInitialSubscription()) {

                    // Get the details of the referring customer
                    CustomerEntity referringCustomer = customerRegistrationRepository.findUserByReferralCode(customer.getReferredByCode());

                    // send Ksh 50 to the referring customer represented as 5 for test
                    merchantToBeneficiary(referringCustomer.getCustomerAccountNumber(), 5, customer.getCustomerAccountNumber());

                    // send Ksh 15 to the subscribing  customer represented as 1 for test
                    merchantToBeneficiary(customer.getCustomerAccountNumber(), 1, customer.getCustomerAccountNumber());

                }
                if (!customer.getIsInitialSubscription()) {
                    logger.info("===Customer's Initial Subscription ===");
                    logger.info("Initial subscription  :  {}", customer.getIsInitialSubscription());
                    logger.info("ReferredByCode  :  {}", customer.getReferredByCode());

                    // Get the details of the referring customer
                    CustomerEntity referringCustomer = customerRegistrationRepository.findUserByReferralCode(customer.getReferredByCode());
                    logger.info("==== REFERRING CUSTOMER =====");
                    logger.info("Full Names   :  {}", referringCustomer.getDisplayName());
                    logger.info("Account Number  : {}", referringCustomer.getCustomerAccountNumber());

                    merchantToBeneficiary(referringCustomer.getCustomerAccountNumber(), 1, customer.getCustomerAccountNumber());
                }
            }
        }

//        if (subscription != null && Objects.equals(subscription.getPlanName(), "Test")) {
//            logger.info("Subscription Plan  : > {}", subscription.getPlanName());
//
//            CustomerEntity customer = customerRegistrationRepository.getUserByAccNo(subscription.getBeneficiaryAccountNumber());
//            logger.info("===SUBSCRIBED CUSTOMER  DETAILS===");
//            logger.info("Customer Details  : {}", customer.getCustomerAccountNumber());
//            logger.info("Full Names  : {}", customer.getDisplayName());
//            logger.info("First Time Subscription  :{}", customer.getIsInitialSubscription());
//            logger.info("Referred By Code  : {}", customer.getReferredByCode());
//
//            // Find the refferring customer using the `referredByCode`
//            CustomerEntity referringCustomer = customerRegistrationRepository.findUserByReferralCode(customer.getReferredByCode());
//            logger.info("==== REFERRING CUSTOMER====");
//            logger.info("FUll Names   :{}", referringCustomer.getDisplayName());
//            logger.info("Account Number   :{}", referringCustomer.getCustomerAccountNumber());
//
//            // if it is the customer's initial subscription , credit the  customer a 6% discount and the refrring customer
//            // a commission of Ksh . 50
////            if (customer.getIsInitialSubscription() && customer.getReferredByCode()!=null){
////                logger.info("==== CREDIT DISCOUNT AND REFERRAL COMMISSION =====");
////                logger.info("SENDING KSH 50 AND 15 TO REFFERING CUSTOME AND SUBSCRIBING CUSTOMERS RESPECTIVELY");
////            }else if(customer.getIsInitialSubscription() && customer.getReferredByCode()==null) {
////                logger.info("CREDITING KSH 15 DISCOUNT TO THE SUBSCRIBING CUSTOMER");
////            }
//
//        }

    }


    public MerchantToBeneficiaryResponse merchantToBeneficiary(String referringCustomerAccNo, Integer amount, String subscibingCustomerAccNo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(merchantToBeneficiarryApi.getAccessToken().getAccessToken());
        Map<String, Object> merchantToBeneficiary = new HashMap<>();
        merchantToBeneficiary.put("TransactionReference", HelperUtility.getTransactionUniqueNumber());
        merchantToBeneficiary.put("BeneficiaryAccountNumber", referringCustomerAccNo);
        merchantToBeneficiary.put("Amount", amount);
        merchantToBeneficiary.put("SenderMerchantCode", "122122");
        merchantToBeneficiary.put("ReceiverMerchantCode", "669994");
        merchantToBeneficiary.put("Reason", "Referral Commission");
        merchantToBeneficiary.put("TransactionFee", 0);
        merchantToBeneficiary.put("CallBackUrl", sasaPayConfig.getMerchantToBeneficiarryCallBack());
//        merchantToBeneficiary.put("CallBackUrl", "https://4476-197-248-98-3.eu.ngrok.io/v1/merchant-to-beneficiarry/validation");

        HttpEntity<Map<String, Object>> merchantToBeneficiaryRequest = new HttpEntity<>(merchantToBeneficiary, headers);
        logger.info("Merchant To Benefiary Request {}", merchantToBeneficiaryRequest);
        ResponseEntity<MerchantToBeneficiaryResponse> merchantToBeneficiaryRespones = restTemplate.postForEntity(
                "https://api.sasapay.app/api/v1/payments/b2c/beneficiary/",
                merchantToBeneficiaryRequest, MerchantToBeneficiaryResponse.class);

        if (merchantToBeneficiaryRespones.getStatusCode() == HttpStatus.OK) {
            System.out.println("Merchant to Beneficiary Request Successful");
            logger.info("Referrer has been credited");
            logger.info("Referrer has been credited" + merchantToBeneficiaryRespones.getBody());
            // update subscription table
            // also update user's record  set `is_subscribed` status to true, set `is_initial_subscription` status to false
            // set `is_renewal` status to true
            subscriptionRepository.updateSubscriptionTable(subscibingCustomerAccNo);
            customerRegistrationRepository.updateUsersSubscriptionStatus(subscibingCustomerAccNo);
            System.out.println(merchantToBeneficiaryRespones.getBody());
            // TODO: save request and response in `all_transactions` table
            return merchantToBeneficiaryRespones.getBody();
        } else {
            return merchantToBeneficiaryRespones.getBody();
        }
    }
}

// Function 1 : Credit 6 % commission to first time subscribers without who signed up without an invitation code
// Function 2 : credit Ksh 50 for the first time to the person who referred the user subscibing
// Function 3 : update x`

