package com.teleeza.wallet.teleeza.customer_registration.repository;

import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TopPerfomers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CustomerRegistrationRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByMobileNumber(String phone);

    @Query(value = "SELECT * FROM users WHERE phone=?1", nativeQuery = true)
    CustomerEntity findCustomerByPhoneNumber(String mobileNumber);

    CustomerEntity findByCustomerAccountNumber(String accountNumber);

    @Query(value = "SELECT * from users  WHERE account_number= ?1", nativeQuery = true)
    CustomerEntity getUserByAccNo(String accountNumber);

    //Get subscription status of a user.
    @Query(value = "select is_subscribed from users where phone=?1", nativeQuery = true)
    Boolean getUsersSubscriptionStatus(String phone);

    @Query(value = "SELECT * FROM users where referral_code =?1 and referral_code is not null", nativeQuery = true)
    CustomerEntity findUserByReferralCode(String referredby_code);
//    CustomerEntity findByReferredByCode(String code);

    CustomerEntity findCustomerEntityByReferralCodeAndReferralCodeNotNull(String referredByCode);

    List<CustomerEntity> findAllByReferredByCode(String code); // pass the code of the user who reffered

    Long countAllByReferredByCode(String code);

    Boolean existsByReferralCode(String referralCode);

    // Sets user's subnscription status to active when they pay Ksh 250
    @Modifying
    @Query(value = "update users set is_initial_subscription = false,is_renewal=true , is_subscribed = true where referral_code=?1", nativeQuery = true)
    void updateUsersSubscriptionStatus(String referralCode);

    @Query(value = "SELECT * from users where expiration_date <= CURRENT_DATE()  and is_subscribed = true limit  1", nativeQuery = true)
    CustomerEntity getExpiredSubscription();



    // Updates subscription status to incative when it expires
    @Modifying
    @Query(value = "update users  set is_initial_subscription = false , is_renewal = true , is_subscribed = false where phone = ?1", nativeQuery = true)
    void updateUserSubscriptionStatus(String phone);

    @Query(name = "topEarners", nativeQuery = true)
    List<TopPerfomers> getTopEarners();


    Boolean existsByMobileNumber(String mobileNumber);

    Boolean existsByEmail(String emailAddress);


    @Modifying
    @Query(value = "update users set otp = ?1 where phone = ?2 and email = ?3  ", nativeQuery = true)
    void saveOtp(String otp, String phone, String email);

    @Modifying
    @Query(value = "update users set message_id = ?1 where phone = ?2", nativeQuery = true)
    void updateUserRecordWithMessageId(String messageId, String phone);


    @Query(value = "select * from users where phone = ?1", nativeQuery = true)
    CustomerEntity getUserSubscriptionDetails(String phoneNumber);
}
