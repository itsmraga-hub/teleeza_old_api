package com.teleeza.wallet.teleeza.sasapay.cashout.repository;

import com.teleeza.wallet.teleeza.sasapay.cashout.entity.CashoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashoutTransactionsRepository extends JpaRepository<CashoutEntity,Long> {
    CashoutEntity findByTransactionReference(String transactionReference);
}
