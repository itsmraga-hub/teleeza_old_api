package com.teleeza.wallet.teleeza.customer_registration.repository;

import com.teleeza.wallet.teleeza.customer_registration.entities.RegisteredCustomers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredCustomerRepository extends JpaRepository<RegisteredCustomers,Long> {


}
