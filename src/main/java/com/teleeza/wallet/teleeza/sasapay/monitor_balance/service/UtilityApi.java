package com.teleeza.wallet.teleeza.sasapay.monitor_balance.service;


import com.teleeza.wallet.teleeza.sasapay.monitor_balance.dtos.responses.AccessTokenResponse;
import com.teleeza.wallet.teleeza.sasapay.monitor_balance.dtos.responses.BalanceResponse;

public interface UtilityApi {
    AccessTokenResponse getAccessToken();

    BalanceResponse getBalances(String MerchantCode);
}
