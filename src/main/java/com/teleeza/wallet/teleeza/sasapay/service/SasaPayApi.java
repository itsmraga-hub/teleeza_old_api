package com.teleeza.wallet.teleeza.sasapay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teleeza.wallet.teleeza.sasapay.authentication.dtos.responses.AccessTokenResponse;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.requests.InternalBillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.bill_payment.dtos.responses.BillPaymentResponse;
import com.teleeza.wallet.teleeza.sasapay.cashout.dtos.requests.InternalCashoutRequest;
import com.teleeza.wallet.teleeza.sasapay.cashout.dtos.responses.CashoutResponse;
import com.teleeza.wallet.teleeza.common.AcknowledgeResponse;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests.CustomerConfirmationRequest;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests.CustomerRegistrationRequest;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests.KycUpdateRequest;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.CustomerConfirmationResponse;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.CustomerRegistrationResponse;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.KycUpdateResponse;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests.InternalLoadCustomerWalletRequest;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests.TopUpAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.requests.TopUpVerificationRequest;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.responses.TopupVerification;
import com.teleeza.wallet.teleeza.sasapay.load_wallet.dtos.responses.LoadCustomerWalletResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests.BankTransferAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.requests.InternalBankTransfer;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.dtos.response.BankTransferResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests.InternalMobileMoneyTransferRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.requests.MobileMoneyTransferAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.dtos.responses.CustomerToMobileMoneyResponse;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.request.CustomerToCustomerAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.request.InternalCustomerToCustomerRequest;
import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.dtos.response.CustomerToCustomerResponse;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests.InternalTillPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.requests.TillPaymentAsyncRequest;
import com.teleeza.wallet.teleeza.sasapay.till_payment.dtos.responses.TillPaymentResponse;

public interface SasaPayApi {
    AccessTokenResponse getAccessToken();

    CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest customerRegistrationRequest);

    CustomerConfirmationResponse customerRegistrationConfirmation(
            CustomerConfirmationRequest customerConfirmationRequest
    );

    KycUpdateResponse updateUserKyc(KycUpdateRequest kycUpdateRequest);

    LoadCustomerWalletResponse merchantLoadCustomerWallet(
            InternalLoadCustomerWalletRequest internalLoadCustomerWalletRequest
    );

    CustomerToCustomerResponse customerToCustomerSendMoney(
            InternalCustomerToCustomerRequest internalCustomerToCustomerRequest
    ) throws JsonProcessingException;

    CustomerToMobileMoneyResponse customerToMobileMoney(InternalMobileMoneyTransferRequest internalMobileMoneyTransferRequest);

    // Make payment to paybills registerd on sasapay
    BillPaymentResponse payBills(InternalBillPaymentRequest internalBillPaymentRequest);
    // Make payment to sasapay till numbers
    TillPaymentResponse payToTills(InternalTillPaymentRequest internalTillPaymentRequest);

    // Withdraw from an agent
    CashoutResponse withDrawFromAgent(InternalCashoutRequest internalCashoutRequest);

    // Send to bank.
    BankTransferResponse sendToBank(InternalBankTransfer internalBankTransfer);

    // Verify topup wallet
    TopupVerification verifyTopUpWallet(TopUpVerificationRequest topUpVerificationRequest);
    //Validate wallet top up transactions
    AcknowledgeResponse validateLoadWallet(TopUpAsyncRequest topUpAsyncRequest);

    AcknowledgeResponse validateMobileTransfer(MobileMoneyTransferAsyncRequest mobileMoneyTransferAsyncRequest);

    AcknowledgeResponse validateBankTransferTransactions(BankTransferAsyncRequest bankTransferAsyncRequest);

    AcknowledgeResponse validateC2CTransaction(CustomerToCustomerAsyncRequest customerToCustomerAsyncRequest);

    AcknowledgeResponse validateTillTransactions(TillPaymentAsyncRequest tillPaymentAsyncRequest);

//    BalanceResponse getBalances(String MerchantCode);


}
