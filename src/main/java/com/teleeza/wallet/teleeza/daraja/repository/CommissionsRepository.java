package com.teleeza.wallet.teleeza.daraja.repository;

import com.teleeza.wallet.teleeza.daraja.entity.Commissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionsRepository extends JpaRepository<Commissions,Long> {
    Commissions findByCategory(String category);
}
