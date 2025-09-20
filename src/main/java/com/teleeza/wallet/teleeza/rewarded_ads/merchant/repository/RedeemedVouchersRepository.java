package com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity.RedeemedVouchers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RedeemedVouchersRepository extends JpaRepository<RedeemedVouchers,Long> {
    List<RedeemedVouchers> findAllByPhoneNumber(String phoneNumber);
    List<RedeemedVouchers> findAllByPhoneNumberAndAdTypeOrderByIdDesc(String phoneNumber, String adType);

    @Query(value = "select * from redeemed_vouchers where phone_number = ?1 order by id desc  limit 10",nativeQuery = true)
    List<RedeemedVouchers> getRecentTenVouchers(String phoneNumber);

    @Query(value = "select sum(value) from redeemed_vouchers where phone_number = ?1 and redeemed_at >= date(now()) - interval ?2 day",nativeQuery = true)

    BigDecimal totalRedeemedByMerchant(String phone, Long dateRange);
}
