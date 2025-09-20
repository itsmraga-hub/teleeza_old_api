package com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.repository;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.postpaid.entity.PostPaidEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostPaidTransactionsRepository extends JpaRepository<PostPaidEntity,Long> {
}
