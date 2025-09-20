package com.teleeza.wallet.teleeza.topups.send_airtime.repository;

import com.teleeza.wallet.teleeza.topups.send_airtime.models.AirtimeAdRewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtopUpAirtimeRepository extends JpaRepository<AirtimeAdRewards,Long> {

    List<AirtimeAdRewards> getAirtimeAdRewardsByMobile(String mobile);

    List<AirtimeAdRewards> getAirtimeAdRewardsByMobileAndAdTypeContainingIgnoreCaseOrderByIdDesc(String mobile, String adType);
}
