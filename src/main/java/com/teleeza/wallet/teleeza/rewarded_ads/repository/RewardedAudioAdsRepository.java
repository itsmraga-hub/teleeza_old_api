package com.teleeza.wallet.teleeza.rewarded_ads.repository;


import com.teleeza.wallet.teleeza.rewarded_ads.entity.AudioAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedAudioAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedVideoAds;
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
public interface RewardedAudioAdsRepository extends JpaRepository<RewardedAudioAds, Long> {
    @Query(value = "select * from rewarded_audio_ads where end_date >=CURRENT_DATE() order by id desc limit 3", nativeQuery = true)
    List<RewardedAudioAds> getMostRecentTenAudioAds();

    @Query(name = "rewardedAudiosAds", nativeQuery = true)
    List<AudioAds> getActiveAudioAdsOnly(String phone);

    @Query(name = "filteredRewardedAudioAds", nativeQuery = true)
    List<AudioAds> getRewardedAudiAdsByIndustry(String phone, String industry);

    Optional<RewardedAudioAds> findById(Long id);

    @Query(value = "select sum(amount) from rewarded_audio_ads where end_date>=current_date()  and is_approved = true and is_closed = false", nativeQuery = true)
    BigDecimal totalAmountOfActiveAudioAds();

    @Modifying
    @Transactional
    @Query(value = "UPDATE rewarded_audio_ads\n" +
            "SET is_closed = true\n" +
            "WHERE id = :advertId\n" +
            "AND (SELECT SUM(amount) FROM rewarded_audio WHERE advert_id = :advertId) = target_amount",
            nativeQuery = true
    )
    void closeAd(@Param("advertId") long advertId);

@Query(value = "SELECT SUM(rewarded_audio_ads.amount) AS total_amount\n" +
        "FROM rewarded_audio_ads\n" +
        "LEFT JOIN (\n" +
        "    SELECT advert_id, COUNT(*) AS count\n" +
        "    FROM rewarded_audio\n" +
        "    WHERE phone = ?1\n" +
        "    GROUP BY advert_id\n" +
        ") rewarded_audio\n" +
        "ON rewarded_audio_ads.id = rewarded_audio.advert_id\n" +
        "WHERE rewarded_audio_ads.end_date >= CURRENT_DATE()\n" +
        "AND rewarded_audio_ads.is_approved = true\n" +
        "  AND rewarded_audio_ads.is_closed = false\n" +
        "AND (\n" +
        "    rewarded_audio.count = 0 OR\n" +
        "    rewarded_audio.count IS NULL\n" +
        ")",nativeQuery = true)
    BigDecimal totalAmountOfUnwatchedAudioAds(@Param("phone")String phone);
}
