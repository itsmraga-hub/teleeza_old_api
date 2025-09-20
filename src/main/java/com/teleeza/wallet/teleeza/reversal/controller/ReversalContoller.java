package com.teleeza.wallet.teleeza.reversal.controller;

import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.notification.model.dtos.requests.PushNotificationRequest;
import com.teleeza.wallet.teleeza.notification.service.PushNotificationService;
import com.teleeza.wallet.teleeza.reversal.dtos.requests.MerchantToBeneficiaryAsyncRequest;
import com.teleeza.wallet.teleeza.reversal.dtos.requests.PayBillsReversalAsyncRequest;
import com.teleeza.wallet.teleeza.reversal.entity.ReversalsEntity;
import com.teleeza.wallet.teleeza.reversal.repository.ReversalRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/teleeza-wallet")
@Slf4j
public class ReversalContoller {

    @Autowired private ReversalRepository reversalRepository;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private  CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private AdvantaSmsApiImpl advantaSmsApiImpl;
    @Autowired
    private TransactionsRepository transactionsRepository;

    @PostMapping("/reversal-validation")
    public ResponseEntity<MerchantToBeneficiaryAsyncRequest> valdiateReversalTransactions(
            @RequestBody MerchantToBeneficiaryAsyncRequest request
    ) {

        CustomerEntity customer = customerRegistrationRepository.findByCustomerAccountNumber(request.getBeneficiaryAccountNumber());
        String phone = ""+customer.getMobileNumber().substring(1);

        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(request.getMerchantRequestID());
        transactions.setCheckoutRequestId(request.getCheckoutRequestID());
        transactions.setResultCode(Integer.parseInt(request.getResultCode()));
        transactions.setResultDesc(request.getResultDesc());
        transactions.setMerchantCode("669994");
        transactions.setTransactionAmount(request.getTransAmount());
        transactions.setMerchantTransactionReference("");
        transactions.setSenderMerchantCode(request.getMerchantCode());
        transactions.setTransactionDate(request.getTransactionDate());
        transactions.setRecipientAccountNumber(request.getBeneficiaryAccountNumber());
        transactions.setDestinationChannel("0");
        transactions.setSourceChannel("0");
        transactions.setRecipientName(customer.getDisplayName());
        transactions.setReason("Reversal");
        transactions.setTransactionFee("0");
        transactions.setSenderName("Teleeza Africa Ltd");
        transactions.setRecipientName(customer.getDisplayName());
        transactions.setBeneficiaryAccNumber(request.getBeneficiaryAccountNumber());
        transactions.setSenderAccountNumber(request.getSenderAccountNumber());
        transactions.setIsTransactionType(true);
        transactionsRepository.save(transactions);

        ReversalsEntity reversals = new ReversalsEntity();
        reversals.setStatusCode(request.getResultCode());
        reversals.setSenderAccountNumber(request.getSenderAccountNumber());
        reversals.setMerchantReference(request.getMerchantRequestID());
        reversals.setMerchantCode(request.getMerchantCode());
        reversals.setAmount(Double.valueOf(request.getTransAmount()));
        reversals.setAccountNumber(request.getBeneficiaryAccountNumber());
        reversalRepository.save(reversals);

        PushNotificationRequest notificationRequest = new PushNotificationRequest();
        notificationRequest.setTitle("Teleeza Wallet");
        notificationRequest.setMessage("Dear  " + customer.getFirstName() +
                ", Your transaction of Ksh." + request.getTransAmount()+
                "with Receipt NO."+ request.getMerchantRequestID() +" has been reversed. Thank you for choosing teleeza." );
        notificationRequest.setToken(customer.getFcmToken());
        notificationRequest.setTopic("Send Money");
        pushNotificationService.sendPushNotificationToToken(notificationRequest);

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PostMapping("/paybill-reversal-validation")
    public ResponseEntity<PayBillsReversalAsyncRequest> validatePaybillsReversals(
            @RequestBody PayBillsReversalAsyncRequest payBillsReversalAsyncRequest){
        CustomerEntity customer = customerRegistrationRepository.findByCustomerAccountNumber(payBillsReversalAsyncRequest.getBeneficiaryAccountNumber());
        String phone = ""+customer.getMobileNumber().substring(1);

        Transactions transactions = new Transactions();
        transactions.setMerchantRequestId(payBillsReversalAsyncRequest.getMerchantRequestID());
        transactions.setCheckoutRequestId(payBillsReversalAsyncRequest.getCheckoutRequestID());
        transactions.setResultCode(payBillsReversalAsyncRequest.getResultCode());
        transactions.setResultDesc(payBillsReversalAsyncRequest.getResultDesc());
        transactions.setMerchantCode("669994");
        transactions.setTransactionAmount(payBillsReversalAsyncRequest.getTransAmount());
        transactions.setMerchantTransactionReference("");
        transactions.setSenderMerchantCode(payBillsReversalAsyncRequest.getMerchantCode());
        transactions.setTransactionDate(payBillsReversalAsyncRequest.getTransactionDate());
        transactions.setRecipientAccountNumber(payBillsReversalAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setDestinationChannel("0");
        transactions.setSourceChannel("0");
        transactions.setRecipientName(customer.getDisplayName());
        transactions.setReason("Reversal");
        transactions.setTransactionFee("0");
        transactions.setSenderName("Teleeza Africa Ltd");
        transactions.setRecipientName(customer.getDisplayName());
        transactions.setBeneficiaryAccNumber(payBillsReversalAsyncRequest.getBeneficiaryAccountNumber());
        transactions.setSenderAccountNumber(payBillsReversalAsyncRequest.getSenderMerchantCode());
        transactions.setIsTransactionType(true);
        transactionsRepository.save(transactions);

        return new ResponseEntity<>(payBillsReversalAsyncRequest, HttpStatus.OK);

    }
}
