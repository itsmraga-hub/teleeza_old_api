package com.teleeza.wallet.teleeza.freemium_calculator.service.impl;

import com.teleeza.wallet.teleeza.freemium_calculator.dtos.request.FreemiumCalculatorRequest;
import com.teleeza.wallet.teleeza.freemium_calculator.dtos.response.FreemiumCalculatorResponse;
import com.teleeza.wallet.teleeza.freemium_calculator.service.FreemiumCalculatorApi;
import org.springframework.stereotype.Service;

@Service
public class FreemiumCalculatorImpl implements FreemiumCalculatorApi {
    @Override
    public FreemiumCalculatorResponse calculateFreemium(FreemiumCalculatorRequest request) {
        FreemiumCalculatorResponse response = new FreemiumCalculatorResponse();

        if (request.getSubscriptionPlan().equals("Monthly")) {
            response.setCalculatedPrice(String.valueOf(349 + (request.getAdditionalFamilyMembers() * 300)));
//            response.setCalculatedPrice(String.valueOf( 1 + (request.getAdditionalFamilyMembers() * 1)));
            return response;
        } else if (request.getSubscriptionPlan().equals("Quarterly")) {
            response.setCalculatedPrice(String.valueOf(1037 + (request.getAdditionalFamilyMembers() * 891)));
            return response;
        } else if (request.getSubscriptionPlan().equals("Semi-Annual")) {
            response.setCalculatedPrice(String.valueOf(2052 + (request.getAdditionalFamilyMembers() * 1764)));
            return response;
        } else if (request.getSubscriptionPlan().equals("Annual")) {
            response.setCalculatedPrice(String.valueOf(4062 + (request.getAdditionalFamilyMembers() * 3492)));
            return response;
        } else {
            return response;
        }
    }
}
