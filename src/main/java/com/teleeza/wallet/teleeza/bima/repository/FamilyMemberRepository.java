package com.teleeza.wallet.teleeza.bima.repository;

import com.teleeza.wallet.teleeza.bima.entities.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Long> {

    Boolean existsByPrincipalPhoneNumber(String phoneNumber);

    @Query(value = "select count(*) from family_members where principal_phone_number = ?1",nativeQuery = true)
    Long getFamilyMembersCount(String phoneNumber);

    List<FamilyMember> getFamilyMemberByCustomerId(Long id);
}
