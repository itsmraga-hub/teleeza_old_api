package com.teleeza.wallet.teleeza.daraja.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushAsyncResponse;

import java.util.ArrayList;
import java.util.List;

public class PaymentCallBackSubject {
    private final List<DarajaObserver> observers = new ArrayList<>();

    public void registerObserver(DarajaObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(DarajaObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(StkPushAsyncResponse response) throws JsonProcessingException {
        for (DarajaObserver observer : observers) {
            observer.update(response);
        }
    }

    public void handleCallback(StkPushAsyncResponse response) throws JsonProcessingException {
        // Perform any necessary processing before notifying observers
        notifyObservers(response);
    }
}
