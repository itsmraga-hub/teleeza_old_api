package com.teleeza.wallet.teleeza.sasapay.bill_payment.repository;

import com.teleeza.wallet.teleeza.sasapay.bill_payment.entity.BillsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillsEntity,Long> {
    BillsEntity findByTransactionReference(String transactionReference);
}
