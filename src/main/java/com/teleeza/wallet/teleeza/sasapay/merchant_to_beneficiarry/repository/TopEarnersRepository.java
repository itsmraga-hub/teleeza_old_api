package com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.repository;

import com.teleeza.wallet.teleeza.sasapay.merchant_to_beneficiarry.entity.TopEarners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopEarnersRepository extends JpaRepository<TopEarners,Long> {

    @Query(value = "select * from top_earners order by amount desc limit 15",nativeQuery = true)
    List<TopEarners> getTopEarnersLimitBy15();
}
