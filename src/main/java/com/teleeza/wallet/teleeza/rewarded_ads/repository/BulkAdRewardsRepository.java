package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.BulkAdRewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BulkAdRewardsRepository extends JpaRepository<BulkAdRewards, Long> {
    Optional<BulkAdRewards> findByPhoneNumber(String phoneNumber);

}
