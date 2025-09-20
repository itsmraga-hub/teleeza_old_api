package com.teleeza.wallet.teleeza.daraja.repository;

import com.teleeza.wallet.teleeza.daraja.entity.BulkPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkPaymentRepository extends JpaRepository<BulkPayment,Integer> {
}
