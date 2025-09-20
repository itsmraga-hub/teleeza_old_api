package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.Industries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndustriesRepository extends JpaRepository<Industries,Long> {
    @Query(value = "select * from industries order by name asc", nativeQuery = true)
    List<Industries> getAllIndustries();
    List<Industries> getIndustriesByFlagIs(String flag);
    List<Industries> getIndustriesByFlagOrderByNameAsc(String flag);
}
