package com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.service;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.requests.InternalAirtimeRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.dtos.responses.AirtimeResponse;

public interface AirtimeApi {
    AirtimeResponse buyAirtime(InternalAirtimeRequest internalAirtimeRequest);

}
