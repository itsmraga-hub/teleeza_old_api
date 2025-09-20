package com.teleeza.wallet.teleeza.topups.send_airtime.service;

import com.teleeza.wallet.teleeza.topups.send_airtime.controllers.SendAirtimeController;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.requests.SendAirtimeRequest;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.AccessTokenResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.SendAirtimeResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.TupayAirtimeBalanceResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.TupayAirtimeResponse;

public interface IAirtimeService {
    AccessTokenResponse getAccessToken();

    void checkPhoneNumber(String phoneNumber);

    AccessTokenResponse getTupayAccessToken();

    TupayAirtimeBalanceResponse getBalance();

    SendAirtimeResponse sendAirtimeToUser(SendAirtimeRequest sendAirtimeReq);

    TupayAirtimeResponse sendAirtimeToPerson(String account, String amount);

    SendAirtimeResponse sendAirtime(SendAirtimeRequest sendAirtimeRequest);
}
