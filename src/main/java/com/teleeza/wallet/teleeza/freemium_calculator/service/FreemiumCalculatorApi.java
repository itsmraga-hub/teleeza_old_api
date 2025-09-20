package com.teleeza.wallet.teleeza.freemium_calculator.service;

import com.teleeza.wallet.teleeza.freemium_calculator.dtos.request.FreemiumCalculatorRequest;
import com.teleeza.wallet.teleeza.freemium_calculator.dtos.response.FreemiumCalculatorResponse;

public interface FreemiumCalculatorApi {

    FreemiumCalculatorResponse calculateFreemium(FreemiumCalculatorRequest request);
}
