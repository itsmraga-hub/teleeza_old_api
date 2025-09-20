package com.teleeza.wallet.teleeza.subscription.repository;

import com.teleeza.wallet.teleeza.subscription.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation,Long> {

    Boolean existsByOrganisationCode(String organisationCode);

    Organisation findByOrganisationCode(String code);


}
