package com.teleeza.wallet.teleeza.bima.repository;

import com.teleeza.wallet.teleeza.bima.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Boolean existsByMobileNumberOrIdNumber(String mobileNumber, String idNumber);

    Customer findByMobileNumber(String mobileNumber);

    Customer findCustomerByMobileNumberOrIdNumber(String phoneNumber,String idNumber);

    @Query(value = "select * from customers where mobile_number = ?1", nativeQuery = true)
    Customer getCustomerByMobile(String mobileNumber);

    @Query(value = "select * from customers where id = ?1", nativeQuery = true)
    Customer getCustomerById(Long id);
}
