package com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.repository;

import com.teleeza.wallet.teleeza.sasapay.utilitypayments.kplc.entity.TokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KplcTokenRepository extends JpaRepository<TokensEntity,Long> {
}
