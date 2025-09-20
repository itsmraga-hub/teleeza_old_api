package com.teleeza.wallet.teleeza.sasapay.till_payment.repository;

import com.teleeza.wallet.teleeza.sasapay.till_payment.entity.TillsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TillsTransactionRepository extends JpaRepository<TillsEntity,Long> {
    TillsEntity findByTransactionReference(String transactionReference);
}
