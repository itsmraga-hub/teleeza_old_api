package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VouchersRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    Optional<Voucher> findVoucherByCodeAndAdvertIdAndPhoneNumberAndAdType(String code, Long advertId, String phoneNumber, String adType);

    @Query(value = "select * from vouchers where phone_number = ?1 order by id desc limit 10",nativeQuery = true)
    List<Voucher> findAllByPhoneNumber(String phoneNumber);

    List<Voucher> findAllByPhoneNumberAndAdTypeContainingIgnoreCaseOrderByIdDesc(String phoneNumber, String adType);

    @Query(value = "select * from  vouchers where phone_number = ?1 order by id desc limit 100",nativeQuery = true)
    List<Voucher> getUserVouchers(String phoneNumber);

    Voucher findVoucherByPhoneNumberAndAdvertIdAndAdTitle(String phoneNumber, Long advertId, String adTitle);
    Boolean existsByPhoneNumberAndAdvertIdAndAdTitle(String phoneNumber, Long advertId, String adTitle);
}
