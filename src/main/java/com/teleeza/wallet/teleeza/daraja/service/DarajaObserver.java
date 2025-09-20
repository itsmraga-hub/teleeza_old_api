package com.teleeza.wallet.teleeza.daraja.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushAsyncResponse;

public interface DarajaObserver {
    void update(StkPushAsyncResponse stkPushAsyncResponse) throws JsonProcessingException;
}
