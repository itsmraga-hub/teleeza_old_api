package com.teleeza.wallet.teleeza.subscription.repository;

import com.teleeza.wallet.teleeza.subscription.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity,Long> {

    SubscriptionEntity findByBeneficiaryAccountNumber(String beneficiaryAccountNumber);

    SubscriptionEntity findByMerchantReference(String merchantReference);
    @Query(value = "SELECT * FROM subscriptions a WHERE credited = false and picked = false LIMIT  1 ",nativeQuery = true)
    SubscriptionEntity  getUncreditedSubscriptionsLimitByOne();

    @Query(value = "SELECT * from subscriptions where expiration_date <= current_date()  and is_subscription_satus = true limit  1",nativeQuery = true)
    SubscriptionEntity getExpiredSubscription();


    @Query(value = "SELECT * from subscriptions where expiration_date = current_date() and is_subscription_satus = true limit  1",nativeQuery = true)
    SubscriptionEntity getUserExpiringSubscription();

    @Query(value = "SELECT * FROM subscriptions  WHERE  referrer_credited= false ",nativeQuery = true)
    List<SubscriptionEntity> getUnCreditedSubscriptions();

    @Modifying
    @Query(value = "update subscriptions inner join validated_transactions on subscriptions.merchant_reference = validated_transactions.merchant_request_id set credited = true where beneficiary_account_number =?1 ",nativeQuery = true)
    void updateSubscriptionTable(String beneficiaryAccNo);

    @Modifying
    @Query(value = "update subscriptions set is_subscription_satus = false where is_subscription_satus = true and beneficiary_account_number =?1 ",nativeQuery = true)
    void updateSubscriptionTableInfo(String beneficiary_account_number);

//
}
