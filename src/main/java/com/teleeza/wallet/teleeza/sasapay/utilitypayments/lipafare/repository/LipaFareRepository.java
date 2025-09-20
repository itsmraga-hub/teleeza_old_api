package com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.repository;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.lipafare.entity.LipaFareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LipaFareRepository extends JpaRepository<LipaFareEntity,Long> {
}
