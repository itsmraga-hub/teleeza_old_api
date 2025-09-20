package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedAdsTransactionsAttempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardedAdsTransactionsAttemptsRepository extends JpaRepository<RewardedAdsTransactionsAttempts,Long> {
    RewardedAdsTransactionsAttempts findByMerchantRequestId(String merchantRequestId);

    RewardedAdsTransactionsAttempts findByConversationIDOrOriginatorConversationID(String conversationID,String originatorConversationId);
}
