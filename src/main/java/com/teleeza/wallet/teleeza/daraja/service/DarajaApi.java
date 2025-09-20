package com.teleeza.wallet.teleeza.daraja.service;


import com.google.api.client.json.Json;
import com.teleeza.wallet.teleeza.daraja.account_balance.dtos.response.CheckAccountBalanceResponse;
import com.teleeza.wallet.teleeza.daraja.auth.AccessTokenResponse;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.requests.InternalB2CTransactionRequest;
import com.teleeza.wallet.teleeza.daraja.b2c.dtos.responses.B2CTransactionResponse;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.requests.SimulateTransactionRequest;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.responses.RegisterUrlResponse;
import com.teleeza.wallet.teleeza.daraja.c2b.dtos.responses.SimulateTransactionResponse;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushResponse;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.requests.InternalStkPushRequest;
import com.teleeza.wallet.teleeza.rewarded_ads.dtos.Request.RewardRequest;

public interface DarajaApi {
    AccessTokenResponse getAccessToken();

    AccessTokenResponse getB2CAccessToken();

    RegisterUrlResponse registerUrl();

    SimulateTransactionResponse simulateC2BTransaction(SimulateTransactionRequest simulateTransactionRequest);

    B2CTransactionResponse performB2CTransaction(InternalB2CTransactionRequest internalB2CTransactionRequest);

    StkPushResponse performStkPushTransaction(InternalStkPushRequest internalStkPushRequest);

    B2CTransactionResponse sendReferralCommission(String phoneNumber ,String amount);
    B2CTransactionResponse creditUserForRewardedAds(RewardRequest rewardRequest);
    B2CTransactionResponse sendMoney(String phoneNUmber, int amount);
    B2CTransactionResponse creditMerchantForRedeemedVouchers(String voucherCode,String merchantPhoneNumber,String amount);

    CheckAccountBalanceResponse checkAccountBalance();

}
