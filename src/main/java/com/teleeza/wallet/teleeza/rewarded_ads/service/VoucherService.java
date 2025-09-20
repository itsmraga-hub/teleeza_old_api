package com.teleeza.wallet.teleeza.rewarded_ads.service;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.Voucher;
import com.teleeza.wallet.teleeza.rewarded_ads.repository.VouchersRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class VoucherService {
    @Autowired
    private VouchersRepository voucherRepository;

    public Voucher generateVoucher(
            BigDecimal value,
            String company,
            String phoneNumber,
            String brand,
            String adTitle,
            String adType,
            Long advertId,
            String thumbnail
    ) {

        String code = brand.toUpperCase().substring(0, 1).replace(" ", "") + "-" + generateVoucherCode();
        log.info("Brand : {}", brand);
        while (voucherRepository.findByCode(code).isPresent()) {
            code = brand.toUpperCase() + "-" + generateVoucherCode();
        }
        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setValue(value);
        voucher.setVoucherValid(true);
        voucher.setCompany(company);
        voucher.setSystemGenerated(true);
        voucher.setExpirationDate(LocalDateTime.now().plusDays(30));
        voucher.setCreatedAt(LocalDateTime.now());
        voucher.setPhoneNumber(phoneNumber);
        voucher.setAdTitle(adTitle);
        voucher.setAdType(adType);
        voucher.setAdvertId(advertId);
        voucher.setThumbnail(thumbnail);

        voucherRepository.save(voucher);

        return voucher;

    }

    public Boolean existsByPhoneNumberAndAdvertIdAndAdTitle(
            String phoneNumber, Long advertId, String adTitle
    ) {
        return voucherRepository.existsByPhoneNumberAndAdvertIdAndAdTitle(phoneNumber, advertId, adTitle);
    }


    public static String generateVoucherCode() {
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
        String coupon = stringGenerator.generate(6).toUpperCase();
        log.info("Coupon : {}", coupon);
        return coupon;
    }

}
