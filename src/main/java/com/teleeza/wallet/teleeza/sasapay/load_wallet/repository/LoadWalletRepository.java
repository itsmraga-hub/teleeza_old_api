package com.teleeza.wallet.teleeza.sasapay.load_wallet.repository;

import com.teleeza.wallet.teleeza.sasapay.load_wallet.entities.LoadWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadWalletRepository extends JpaRepository<LoadWalletEntity, Long> {
    LoadWalletEntity findByMerchantReferenceOrTransactionRef(String merchantReference, String transactionRef);

    LoadWalletEntity findByTransactionRef(String transactionRef);

    LoadWalletEntity findByMerchantReference(String merchantReference);
//    @Query("select  sum(transAmount) as topupToday from transactions_attempts where date= current_date and mobileNumber=?1")
//    Long totalTopUpToday(String phone);
}
