package com.teleeza.wallet.teleeza.sasapay.transactions.entity;

import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TopPerfomersDao {
    @Autowired
    TransactionsRepository transactionsRepository;
    @Autowired
    CustomerRegistrationRepository customerRegistrationRepository;


//    public List<TopPerfomers> getCustomer(){
//        return customerRegistrationRepository.topEarners();
//    }
}
