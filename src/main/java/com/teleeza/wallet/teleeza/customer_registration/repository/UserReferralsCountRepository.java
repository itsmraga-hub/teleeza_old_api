package com.teleeza.wallet.teleeza.customer_registration.repository;

import com.teleeza.wallet.teleeza.customer_registration.entities.UserRefarralsCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserReferralsCountRepository extends JpaRepository<UserRefarralsCount, Integer> {

    Optional<UserRefarralsCount> findByAccountNumber(String accountNumber);

    Optional<Boolean> existsByAccountNumber(String accountNumber);
//    @Query(value = "update user_referrals_count set amount = ?1 , count= ?1 where account_number = ?1", nativeQuery = true)
//    UserRefarralsCount updateRecord(Integer amount, Inte);

}
