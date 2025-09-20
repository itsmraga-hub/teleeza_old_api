package com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findMerchantByMerchantNameAndPhone(String merchantName, String phone);

    Merchant findMerchantByPhone(String phone);

    Boolean existsByPhone(String phone);
}
