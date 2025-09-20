package com.teleeza.wallet.teleeza.daraja.repository;

import com.teleeza.wallet.teleeza.daraja.entity.MpesaTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface MpesaTransactionsRepository extends JpaRepository<MpesaTransactions,Long> {
    MpesaTransactions findByMerchantRequestId(String merchantRequestId);

    MpesaTransactions findByConversationIDOrOriginatorConversationID(String conversationID,String originatorConversationId);

    @Query(value = "select sum(amount) from mpesa_transactions where transaction_desc != 'Freemium Subscription' and result_code = 0 and phone_number = ?1", nativeQuery = true)
    BigDecimal totalAmountEarned(String phone);


}
