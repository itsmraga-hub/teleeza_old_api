package com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.service;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.requests.InternalTvPaymentRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.dtos.responses.TvPaymentResponse;

public interface TvPaymentApi {

    TvPaymentResponse payForTv(InternalTvPaymentRequest internalTvPaymentRequest);
}
