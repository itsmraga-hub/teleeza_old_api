package com.teleeza.wallet.teleeza.sasapay.sendmoney.bank;

import com.teleeza.wallet.teleeza.sasapay.sendmoney.bank.entity.BankTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransferRepository extends JpaRepository<BankTransferEntity, Long> {
    BankTransferEntity findByTransactionReference(String transactionRef);
}
