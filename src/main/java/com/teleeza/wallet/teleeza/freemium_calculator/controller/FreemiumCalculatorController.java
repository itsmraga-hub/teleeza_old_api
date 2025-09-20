package com.teleeza.wallet.teleeza.freemium_calculator.controller;

import com.teleeza.wallet.teleeza.freemium_calculator.dtos.request.FreemiumCalculatorRequest;
import com.teleeza.wallet.teleeza.freemium_calculator.dtos.response.FreemiumCalculatorResponse;
import com.teleeza.wallet.teleeza.freemium_calculator.service.FreemiumCalculatorApi;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
@AllArgsConstructor
public class FreemiumCalculatorController {
    private final FreemiumCalculatorApi freemiumCalculatorApi;


    @PostMapping("freemium-calculator")
    public ResponseEntity<FreemiumCalculatorResponse> freemiumCalculator(@RequestBody FreemiumCalculatorRequest request){
        return ResponseEntity.ok(freemiumCalculatorApi.calculateFreemium(request));
    }

}
