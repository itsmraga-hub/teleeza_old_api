package com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.repository;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.airtime.entity.AirtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirtimeRepository extends JpaRepository<AirtimeEntity, Long> {
    AirtimeEntity findByBeneficiaryAccNumber(String beneficiaryAccNumber);
}
