package com.teleeza.wallet.teleeza.sasapay.authentication.controllers;

import com.teleeza.wallet.teleeza.sasapay.authentication.dtos.responses.AccessTokenResponse;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/wallet-auth")
@Slf4j
public class AuthController {

    private final SasaPayApi sasapayApi;

    public AuthController(SasaPayApi sasapayApi) {
        this.sasapayApi = sasapayApi;
    }

    @GetMapping(path = "/token", produces = "application/json")
    public ResponseEntity<AccessTokenResponse> getAccessToken() {
        return ResponseEntity.ok(sasapayApi.getAccessToken());
    }
}
