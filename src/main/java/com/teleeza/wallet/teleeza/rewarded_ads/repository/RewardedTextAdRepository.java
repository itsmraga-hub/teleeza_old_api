package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedAudioAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedTextAd;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedVideoAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.TextAds;
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
public interface RewardedTextAdRepository extends JpaRepository<RewardedTextAd, Long> {
    @Query(value = "select * from rewarded_text_ads  where end_date >= CURRENT_DATE() order by id desc",nativeQuery = true)
    List<RewardedTextAd>  getActiveRewardedTextAds();

    @Query(name = "rewardedTextAds", nativeQuery = true)
    List<TextAds> getRecentFiveAds(String phone);

//    @Query(value = "select * from rewarded_text_ads where industry = ?1 order by id desc ", nativeQuery = true )
    @Query(name = "filteredTextAds", nativeQuery = true)
    List<TextAds> getRewardedTextAdsByIndustry(String phone ,String industry);

    @Query(value = "select sum(amount) from rewarded_text_ads where end_date>=CURRENT_DATE()  and is_approved = true and is_closed = false", nativeQuery = true)
    BigDecimal totalAmountOfActiveTextAds();

    Optional<RewardedTextAd> findById(Long id);

    // RewardedTextAd findById(Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE rewarded_text_ads\n" +
            "SET is_closed = true\n" +
            "WHERE id = :advertId\n" +
            "AND (SELECT SUM(amount) FROM rewarded_texts WHERE advert_id = :advertId) = target_amount",
            nativeQuery = true
    )
    void closeAd(@Param("advertId") long advertId);

    @Modifying
    @Transactional
    @Query(value = "update rewarded_text_ads set amount_paid_out = amount_paid_out + :amount where id = :advertId",
            nativeQuery = true
    )
    void updateAmountPaidOut(
            @Param("advertId") long advertId,
            @Param("amount") BigDecimal amount
    );


    @Query(value = "SELECT SUM(amount) FROM rewarded_texts WHERE advert_id = :advertId", nativeQuery = true)
    BigDecimal sumOfRewardedTextAds(String advertId);

    @Query(value = "SELECT SUM(rewarded_text_ads.amount) AS total_amount\n" +
            "FROM rewarded_text_ads\n" +
            "LEFT JOIN (\n" +
            "    SELECT advert_id, COUNT(*) AS count\n" +
            "    FROM rewarded_texts\n" +
            "    WHERE phone = ?1\n" +
            "    GROUP BY advert_id\n" +
            ") rewarded_texts\n" +
            "ON rewarded_text_ads.id = rewarded_texts.advert_id\n" +
            "WHERE rewarded_text_ads.end_date >= CURRENT_DATE()\n" +
            "AND rewarded_text_ads.is_approved = true\n" +
            "  AND rewarded_text_ads.is_closed = false\n" +
            "AND (\n" +
            "    rewarded_texts.count = 0 OR\n" +
            "    rewarded_texts.count IS NULL\n" +
            ")",nativeQuery = true)
    BigDecimal totalAmountOfUnwatchedTextAds(@Param("phone")String phone);
}
