package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.TextRewards;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredRewardAnswers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface SponsoredAnswersRepository extends JpaRepository<SponsoredRewardAnswers, Long> {
    SponsoredRewardAnswers findByPhoneAndAdvertId(String phone, int advertId);

    @Query(value = "select sum(amount) from sponsored_reward_answers where phone = ?1 and result_code = 0", nativeQuery = true)
    BigDecimal totalEarnedByUserFromSponsoredAds(String phone);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sponsored_reward_answers\n" +
            "SET result_code = :resultCode\n" +
            "WHERE phone = :phone\n" +
            "AND advert_id = :advertId",
            nativeQuery = true
    )
    void validatePayment(@Param("resultCode") Integer resultCode, @Param("phone") String phone, @Param("advertId")Long advertId);

    List<SponsoredRewardAnswers> getAllByPhone(String phone);

    List<SponsoredRewardAnswers> getAllByPhoneOrderByIdDesc(String phone);

    List<SponsoredRewardAnswers> getAllByPhoneAndAdvertTypeOrderByIdDesc(String phone, String advertType);

}
