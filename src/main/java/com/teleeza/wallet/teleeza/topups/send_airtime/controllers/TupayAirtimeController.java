package com.teleeza.wallet.teleeza.topups.send_airtime.controllers;

import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.requests.SendAirtimeRequest;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.AccessTokenResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.SendAirtimeResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.TupayAirtimeBalanceResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.dtos.responses.TupayAirtimeResponse;
import com.teleeza.wallet.teleeza.topups.send_airtime.service.AirtimeService;
import com.teleeza.wallet.teleeza.topups.send_airtime.service.IAirtimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
public class TupayAirtimeController {
    private final IAirtimeService airtimeService;

    public TupayAirtimeController(AirtimeService airtimeService) {
        this.airtimeService = airtimeService;
    }

    @GetMapping("/api/tupay/token")
    public AccessTokenResponse getToken(){
        return airtimeService.getTupayAccessToken();
    }

    @GetMapping("/api/tupay/balance")
    public TupayAirtimeBalanceResponse getBalance(){
        return airtimeService.getBalance();
    }

    @PostMapping("/api/tupay/send-airtime")
    public TupayAirtimeResponse sendAirtime(@RequestBody SendAirtimeRequest sendAirtimeRequest){
        log.info("SendAirtimeRequest {}", sendAirtimeRequest);
        // String airtime = airtimeService.checkPhoneNumber(sendAirtimeRequest.getMobile());
        return airtimeService.sendAirtimeToPerson(sendAirtimeRequest.getAccount(), sendAirtimeRequest.getAmount());
    }



}
