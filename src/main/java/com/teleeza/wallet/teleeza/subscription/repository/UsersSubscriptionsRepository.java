package com.teleeza.wallet.teleeza.subscription.repository;

import com.teleeza.wallet.teleeza.subscription.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersSubscriptionsRepository extends JpaRepository<UserSubscription,Long> {

}
