package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.CustomVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomVoucherRepository extends JpaRepository<CustomVoucher, Integer> {
    CustomVoucher findCustomVoucherByAdvertIdAndAdvertType(Integer advertId, String advertType);

    List<CustomVoucher> findCustomVouchersByAdvertIdAndAdvertTypeAndRedeemed(int advertId, String advertType, boolean redeemed);
    List<CustomVoucher> findCustomVouchersByAdvertIdAndAdvertType(Integer advertId, String advertType);

    Boolean existsByAdvertIdAndAdvertTypeAndClientPhoneNumber(int advertId, String advertType, String clientPhoneNumber);
}
