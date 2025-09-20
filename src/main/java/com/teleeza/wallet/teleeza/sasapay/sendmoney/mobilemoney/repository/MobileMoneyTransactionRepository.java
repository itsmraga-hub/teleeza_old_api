package com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.repository;

import com.teleeza.wallet.teleeza.sasapay.sendmoney.mobilemoney.entity.MobileMoneyTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileMoneyTransactionRepository extends JpaRepository<MobileMoneyTransactionEntity, Long> {
    MobileMoneyTransactionEntity findByTransactionReference(String transactionRef);
}
