package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.AudioRewards;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.TextRewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RewardedTextsRepository extends JpaRepository<TextRewards,Long> {
    TextRewards findByPhoneAndAdvertId(String phone, Long advertId);

    @Query(value = "select sum(amount) from rewarded_texts where phone = ?1 and result_code = 0", nativeQuery = true)
    BigDecimal totalEarnedByUserFromTextAds(String phone);

    @Modifying
    @Transactional
    @Query(value = "UPDATE rewarded_texts\n" +
            "SET result_code = :resultCode\n" +
            "WHERE phone = :phone\n" +
            "AND advert_id = :advertId",
            nativeQuery = true
    )
    void validatePayment(@Param("resultCode") Integer resultCode, @Param("phone") String phone, @Param("advertId")Long advertId);

    List<TextRewards> getAllByPhone(String phone);

    List<TextRewards> getAllByPhoneOrderByIdDesc(String phone);
}
