package com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.service;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.requests.InternalKplcRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.dtos.responses.KplcResponse;

public interface KplcApi {
    KplcResponse buyKplcTokens(InternalKplcRequest internalKplcRequest);
}
