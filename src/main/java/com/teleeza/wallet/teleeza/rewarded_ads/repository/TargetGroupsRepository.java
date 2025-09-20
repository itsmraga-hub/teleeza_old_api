package com.teleeza.wallet.teleeza.rewarded_ads.repository;

import com.teleeza.wallet.teleeza.rewarded_ads.entity.TargetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetGroupsRepository extends JpaRepository<TargetGroup, Integer> {
    TargetGroup findByName(String name);
    List<TargetGroup> findTargetGroupsByCompanyName(String companyName);
    List<TargetGroup> findTargetGroupsByCompanyNameAndGroupNameIn(String companyName, List<String> names);

    TargetGroup findByPhoneNumber(String phoneNumber);
}
