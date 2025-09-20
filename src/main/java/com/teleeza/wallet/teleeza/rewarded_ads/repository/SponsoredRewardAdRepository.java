package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedTextAd;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.TextAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredRewardAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SponsoredRewardAdRepository extends JpaRepository<SponsoredRewardAds, Long> {
    // @Query(value = "select * from sponsored_reward_ads  where end_date >= now() order by id desc",nativeQuery = true)
    // List<SponsoredRewardAds> getActiveSponsoredRewardAds();

    @Query(name = "sponsoredRewardAds", nativeQuery = true)
    List<SponsoredAds> getActiveSponsoredRewardAds(String phone);

    //    @Query(value = "select * from rewarded_text_ads where industry = ?1 order by id desc ", nativeQuery = true )
    // @Query(name = "filteredSponsoredAds", nativeQuery = true)
    // List<SponsoredRewardAds> getSponsoredRewardAdsByIndustry(String phone ,String industry);

    @Query(value = "select sum(amount) from sponsored_reward_ads where end_date>=CURRENT_DATE()  and is_approved = true and is_closed = false", nativeQuery = true)
    BigDecimal totalAmountOfActiveSponsoredRewardAds();

    Optional<SponsoredRewardAds> findById(Integer id);

    // RewardedTextAd findById(Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sponsored_reward_ads\n" +
            "SET is_closed = true\n" +
            "WHERE id = :advertId\n" +
            "AND (SELECT SUM(amount) FROM sponsored_reward_answers WHERE advert_id = :advertId) = target_amount",
            nativeQuery = true
    )
    void closeAd(@Param("advertId") long advertId);

    @Modifying
    @Transactional
    @Query(value = "update sponsored_reward_ads set amount_paid_out = amount_paid_out + :amount where id = :advertId",
            nativeQuery = true
    )
    void updateAmountPaidOut(
            @Param("advertId") long advertId,
            @Param("amount") BigDecimal amount
    );


    @Query(value = "SELECT SUM(amount) FROM sponsored_reward_answers WHERE advert_id = :advertId", nativeQuery = true)
    BigDecimal sumOfRewardedTextAds(String advertId);

    @Query(value = "SELECT SUM(sponsored_reward_ads.amount) AS total_amount\n" +
            "FROM sponsored_reward_ads\n" +
            "LEFT JOIN (\n" +
            "    SELECT advert_id, COUNT(*) AS count\n" +
            "    FROM sponsored_reward_answers\n" +
            "    WHERE phone = ?1\n" +
            "    GROUP BY advert_id\n" +
            ") sponsored_reward_answers\n" +
            "ON sponsored_reward_ads.id = sponsored_reward_answers.advert_id\n" +
            "WHERE sponsored_reward_ads.end_date >= CURRENT_DATE()\n" +
            "AND sponsored_reward_ads.is_approved = true\n" +
            "  AND sponsored_reward_ads.is_closed = false\n" +
            "AND (\n" +
            "    sponsored_reward_answers.count = 0 OR\n" +
            "    sponsored_reward_answers.count IS NULL\n" +
            ")",nativeQuery = true)
    BigDecimal totalAmountOfUnwatchedTextAds(@Param("phone")String phone);
}
