package com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.repository;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.tv.entity.TvPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TvPaymentRepository extends JpaRepository<TvPaymentEntity,Long> {
}
