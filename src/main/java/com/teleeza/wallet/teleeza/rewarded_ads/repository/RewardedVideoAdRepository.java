package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.RewardedVideoAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.VideoAds;
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
public interface RewardedVideoAdRepository extends JpaRepository<RewardedVideoAds, Long> {
    @Query(value = "select  * from  rewarded_video_ads where end_date>=current_date() order by id desc limit 10", nativeQuery = true)
    List<RewardedVideoAds> getRewardedVideoAdsLimitByTen();

    //    @Query(value = "select  * from  rewarded_video_ads where end_date >= now()  order by id desc ",nativeQuery = true)
//    @Query(value = "SELECT\n" +
//            "    rewarded_video_ads.title,\n" +
//            "    rewarded_video_ads.description,\n" +
//            "    rewarded_video_ads.file_path,\n" +
//            "    rewarded_video_ads.image,\n" +
//            "    rewarded_video_ads.question,\n" +
//            "    rewarded_video_ads.choicea,\n" +
//            "    rewarded_video_ads.choiceb,\n" +
//            "    rewarded_video_ads.choicec,\n" +
//            "    rewarded_video_ads.answer,\n" +
//            "    rewarded_video_ads.amount,\n" +
//            "    rewarded_video_ads.industry,\n" +
//            "    rewarded_video_ads.duration,\n" +
//            "    rewarded_video_ads.brand_logo,\n" +
//            "    rewarded_video_ads.call_to_action,\n" +
//            "    rewarded_video_ads.call_to_action_text_color,\n" +
//            "    rewarded_video_ads.call_to_action_background_color,\n" +
//            "    rewarded_video_ads.call_to_action_url,\n" +
//            "    rewarded_video_ads.call_to_action_contact,\n" +
//            "    rewarded_video_ads.opinion_question,\n" +
//            "    rewarded_video_ads.whatsapp_contact,\n" +
//            "    (CASE\n" +
//            "        WHEN rewarded_videos.count > 0 THEN 'true'\n" +
//            "        ELSE 'false'\n" +
//            "    END) AS isWatched\n" +
//            "FROM rewarded_video_ads\n" +
//            "LEFT JOIN (\n" +
//            "    SELECT advert_id, COUNT(*) as count\n" +
//            "    FROM rewarded_videos\n" +
//            "    WHERE phone = ?1\n" +
//            "    GROUP BY advert_id\n" +
//            ") rewarded_videos\n" +
//            "ON rewarded_video_ads.id = rewarded_videos.advert_id",nativeQuery = true)
    @Query(name = "rewardedVideos", nativeQuery = true)
    List<VideoAds> getActiveVideoAdsOnly(String phone);


    @Query(name = "filteredRewardedVideos", nativeQuery = true)
    List<VideoAds> getRewardedAdsByIndustry(String phone, String industry);

    Optional<RewardedVideoAds> findById(Long id);

    @Query(value = "select sum(amount) from rewarded_video_ads where end_date>=CURRENT_DATE() and is_approved = true and is_closed = false", nativeQuery = true)
    BigDecimal totalAmountOfActiveVideoAds();

    @Modifying
    @Transactional
    @Query(value = "UPDATE rewarded_video_ads\n" +
            "SET is_closed = true\n" +
            "WHERE id = :advertId\n" +
            "AND (SELECT SUM(amount) FROM rewarded_videos WHERE advert_id = :advertId) = target_amount",
            nativeQuery = true
    )
//    @Query(value = "UPDATE rewarded_video_ads SET is_closed = true WHERE id = :advertId AND (SELECT SUM(amount) FROM rewarded_videos WHERE advert_id = :advertId) >= (SELECT target_amount FROM rewarded_video_ads WHERE id = :advertId)", nativeQuery = true)
    void closeAd(@Param("advertId") long advertId);


    @Query(value = "SELECT SUM(rewarded_video_ads.amount) AS total_amount\n" +
            "FROM rewarded_video_ads\n" +
            "LEFT JOIN (\n" +
            "    SELECT advert_id, COUNT(*) AS count\n" +
            "    FROM rewarded_videos\n" +
            "    WHERE phone = ?1\n" +
            "    GROUP BY advert_id\n" +
            ") rewarded_videos\n" +
            "ON rewarded_video_ads.id = rewarded_videos.advert_id\n" +
            "WHERE rewarded_video_ads.end_date >= CURRENT_DATE()\n" +
            "AND rewarded_video_ads.is_approved = true\n" +
            "  AND rewarded_video_ads.is_closed = false\n" +
            "AND (\n" +
            "    rewarded_videos.count = 0 OR\n" +
            "    rewarded_videos.count IS NULL\n" +
            ")", nativeQuery = true)
    BigDecimal totalAmountOfUnwatchedVideoAds(@Param("phone")String phone);
}
