package com.teleeza.wallet.teleeza.kokotoa.service;

import com.teleeza.wallet.teleeza.kokotoa.dtos.requests.KokotoaPostRequest;
import com.teleeza.wallet.teleeza.kokotoa.dtos.responses.KokotoaPostResponse;

public interface KokotoaApi {

    KokotoaPostResponse sendTransactionToKokotoa(KokotoaPostRequest kokotoaPostRequest);
}
