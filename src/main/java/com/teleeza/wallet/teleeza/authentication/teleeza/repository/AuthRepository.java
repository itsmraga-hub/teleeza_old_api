package com.teleeza.wallet.teleeza.authentication.teleeza.repository;


import com.teleeza.wallet.teleeza.authentication.teleeza.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
@Repository
@Transactional
public interface AuthRepository extends JpaRepository<User, Long> {
//    User findByPhone(String phone);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

//    User findByPhone(String phone);
//    Optional<User> findByUsername(String username);
    Boolean existsByPhone(String username);
    Boolean existsByEmail(String email);
    Boolean existsByEmailAndPhone(String email ,String phone);
    Boolean existsByPhoneAndEmailAndOtp(String phone,String email,String otp);

    @Modifying
    @Query(value = "update users_auth set otp = ?1 where phone = ?2 and email = ?3  ",nativeQuery = true)
    void saveOtp(String otp,String phone,String email);

    @Modifying
    @Query(value = "update users_auth set password = ?1 where phone = ?2 and email = ?3 and otp = ?4",nativeQuery = true)
    void updatePin(String password,String phone,String email , String otp);

    @Modifying
    @Query(value = "update users_auth set password = ?1 where phone = ?2", nativeQuery = true)
    void changePin(String password,String phone);

//    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
//    @Modifying
//    public void updateFailedAttempts(int failAttempts, String email);

}
