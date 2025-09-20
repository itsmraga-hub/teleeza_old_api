package com.teleeza.wallet.teleeza.bima.repository;

import com.teleeza.wallet.teleeza.bima.entities.NextOfKin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NextOfKinRepository extends JpaRepository<NextOfKin,Long> {

    NextOfKin getNextOfKinByCustomerId(Long id);

    Boolean existsByCustomer_Id(Long id);
}
