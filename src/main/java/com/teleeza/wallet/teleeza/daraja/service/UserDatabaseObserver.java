package com.teleeza.wallet.teleeza.daraja.service;

import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.daraja.config.MpesaConfiguration;
import com.teleeza.wallet.teleeza.daraja.entity.MpesaTransactions;
import com.teleeza.wallet.teleeza.daraja.repository.MpesaTransactionsRepository;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushAsyncResponse;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserDatabaseObserver implements DarajaObserver {
    @Autowired
    private MpesaTransactionsRepository mpesaTransactionsRepository;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private  HttpHeaders httpHeaders;
    @Autowired
    private  RestTemplate restTemplate;
    @Autowired
    private  MpesaConfiguration mpesaConfiguration;
    @Autowired
    private DarajaApiImpl darajaApi;
    private CustomerEntity referringCustomer;
    private CustomerEntity referringCustomerLevel2;
    private CustomerEntity referringCustomerLevel3;
    private List<String> referrals = new ArrayList<>();

    public UserDatabaseObserver() {
    }

    @Override
    @Transactional
    public void update(StkPushAsyncResponse stkPushAsyncResponse) {
        MpesaTransactions transactions = mpesaTransactionsRepository.findByMerchantRequestId(stkPushAsyncResponse.getBody().getStkCallback().getMerchantRequestID());
        log.info("Transactions : {}", transactions);
        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(transactions.getAccountReference());
        log.info("Customer : {}", customer);

        if (stkPushAsyncResponse.getBody().getStkCallback().getResultCode() == 0 ) {
            log.info("Request Successful");
//            if(transactions.getSubscriptionPlan().equals("Monthly")){
//                sendUserData(customer.getReferralCode(),"1");
//            } else if (transactions.getSubscriptionPlan().equals("Quarterly")) {
//                    sendUserData(customer.getReferralCode(),"3");
//            } else if (transactions.getSubscriptionPlan().equals("Semi-Annual")) {
//                sendUserData(customer.getReferralCode(),"6");
//            } else if (transactions.getSubscriptionPlan().equals("Annual")) {
//                sendUserData(customer.getReferralCode(),"12");
//            }
        }
    }

//    public void sendUserData(String referralCode,String plan){
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        httpHeaders.setExpires(0);
//
//        Map<String, Object> userInfo = new HashMap<>();
//        userInfo.put("referral_code", referralCode);
//        userInfo.put("plan", plan);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userInfo, httpHeaders);
//        ResponseEntity<Void> response = restTemplate.postForEntity(
//                mpesaConfiguration.getReferralUrl(),
//                request, Void.class);
//
//        if(response.getStatusCode()== HttpStatus.OK){
//            log.info("User info sent successfully");
//        }else {
//            log.info("SOmething went wrong");
//        }
//    }
}
