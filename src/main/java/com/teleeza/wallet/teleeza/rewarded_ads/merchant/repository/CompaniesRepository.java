package com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompaniesRepository extends JpaRepository<Companies,Long> {

    @Query(value = "select c.name from companies c order by name asc", nativeQuery = true)
    List<String> getAllCompanies();

    Companies findByName(String name);

//    List<Companies> companies = en
}
