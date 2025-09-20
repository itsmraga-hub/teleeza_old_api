package com.teleeza.wallet.teleeza.daraja.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teleeza.wallet.teleeza.bima.service.impl.BimaServiceImpl;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushAsyncResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
@Slf4j
public class BimaPolicyObserver implements DarajaObserver {
    @Autowired
    private  BimaServiceImpl bimaService;

    public BimaPolicyObserver() {

    }

    @Override
    public void update(StkPushAsyncResponse stkPushAsyncResponse) throws JsonProcessingException {
        log.info("Create Bima Policy:{}",stkPushAsyncResponse);

        if(stkPushAsyncResponse.getBody().getStkCallback().getResultCode()==0){
            bimaService.creatPolicy(stkPushAsyncResponse);
        }

    }
}
