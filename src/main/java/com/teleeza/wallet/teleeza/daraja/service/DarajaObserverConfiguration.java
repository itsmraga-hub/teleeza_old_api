package com.teleeza.wallet.teleeza.daraja.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DarajaObserverConfiguration {
    @Bean
    public SmsNotificationObserver smsNotificationObserver(){
        return new SmsNotificationObserver();
    }

    @Bean
    public UserDatabaseObserver dbObserver() {
        return new UserDatabaseObserver();
    }

    @Bean
    public PaymentCallBackSubject callbackSubject() {
        return new PaymentCallBackSubject();
    }

    @Bean
    public BimaPolicyObserver bimaPolicyObserver(){
        return new BimaPolicyObserver();
    }

}
