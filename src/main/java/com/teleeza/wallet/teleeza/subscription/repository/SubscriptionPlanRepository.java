package com.teleeza.wallet.teleeza.subscription.repository;

import com.teleeza.wallet.teleeza.subscription.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<PlanEntity,Long> {
}
