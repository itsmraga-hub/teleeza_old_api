package com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.service;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.requests.InternalLipaFareRequest;
import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.dtos.responses.LipaFareResponse;

public interface LipaFareApi {

    LipaFareResponse lipaFare(InternalLipaFareRequest internalLipaFareRequest);
}
