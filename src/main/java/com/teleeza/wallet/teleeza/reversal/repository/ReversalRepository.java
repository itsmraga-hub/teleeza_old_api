package com.teleeza.wallet.teleeza.reversal.repository;


import com.teleeza.wallet.teleeza.reversal.entity.ReversalsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReversalRepository extends JpaRepository<ReversalsEntity, Integer> {
}
