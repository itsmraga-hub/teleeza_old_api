package com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.service;

import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class C2CService {
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;

    public Boolean existsByPhone(String phone){
        return customerRegistrationRepository.existsByMobileNumber(phone);
    }
}
