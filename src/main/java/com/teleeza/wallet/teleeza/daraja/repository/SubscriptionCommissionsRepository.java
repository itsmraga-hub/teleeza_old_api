package com.teleeza.wallet.teleeza.daraja.repository;


import com.teleeza.wallet.teleeza.daraja.entity.SubscriptionCommissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionCommissionsRepository extends JpaRepository<SubscriptionCommissions, Long> {
    SubscriptionCommissions findByCategory(String category);
}
