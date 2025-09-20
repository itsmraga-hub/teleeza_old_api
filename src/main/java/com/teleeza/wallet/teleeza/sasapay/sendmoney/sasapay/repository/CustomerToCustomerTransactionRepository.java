package com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.repository;

import com.teleeza.wallet.teleeza.sasapay.sendmoney.sasapay.entity.CustomerToCustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerToCustomerTransactionRepository extends JpaRepository<CustomerToCustomerEntity, Long> {
    CustomerToCustomerEntity findByTransactionReference(String transactionRef);
}
