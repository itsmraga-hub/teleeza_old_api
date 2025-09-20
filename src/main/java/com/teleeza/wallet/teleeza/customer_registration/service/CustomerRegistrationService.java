package com.teleeza.wallet.teleeza.customer_registration.service;


import com.teleeza.wallet.teleeza.MQTT.PublishUser;
import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.authentication.teleeza.entity.Role;
import com.teleeza.wallet.teleeza.authentication.teleeza.entity.User;
import com.teleeza.wallet.teleeza.authentication.teleeza.repository.AuthRepository;
import com.teleeza.wallet.teleeza.authentication.teleeza.repository.RoleRepository;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests.CustomerRegistrationRequest;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.CustomerRegistrationResponse;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import com.teleeza.wallet.teleeza.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class CustomerRegistrationService {

    private final CustomerRegistrationRepository repository;
    private final TransactionsRepository transactionsRepository;
    @Autowired
    private AuthRepository userRepository;
    @Autowired
    private AdvantaSmsApiImpl advantaSmsApi;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;


    private final PublishUser publishUser;

    public CustomerRegistrationService(CustomerRegistrationRepository repository, TransactionsRepository transactionsRepository, PublishUser publishUser) {
        this.repository = repository;
        this.transactionsRepository = transactionsRepository;
        this.publishUser = publishUser;
    }

    @CacheEvict("user_details")
    public CustomerEntity getRegisteredCustomerDetails(String phoneNumber) {
        return repository.findCustomerByPhoneNumber(phoneNumber);
    }

    @CacheEvict("referrals")
    public List<CustomerEntity> getUsersReferrals(String referralCode) {
        return repository.findAllByReferredByCode(referralCode);
    }

    @CacheEvict(value = "expenses", allEntries = true)
    public List<Transactions> getAllUserExpenses(String accountNumber) {
        return transactionsRepository.getAllExpenseTransactions(accountNumber);
    }

    @CacheEvict(value = "income", allEntries = true)
    public List<Transactions> getAllUserIncome(String accountNumber) {
        return transactionsRepository.getAllIncomeTransactions(accountNumber);
    }


    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) throws MqttException {
        String regex = "[0-9]+";
        String consecutiveNumbersRegex = "([0-9])\\1+";

        String phone = "+254" + customerRegistrationRequest.getMobileNumber().substring(1);

        // check that the pin contains only digits
        if (!customerRegistrationRequest.getPin().matches(regex)) {
            return new CustomerRegistrationResponse(
                    "",
                    "PIN should only contain digits",
                    "1"

            );
        }

        // check that the pin does not have consecutive numbers e.g. 1111
        if (customerRegistrationRequest.getPin().matches(consecutiveNumbersRegex)) {
            return new CustomerRegistrationResponse(
                    "",
                    "PIN should not contain consecutive digits e.g 0000",
                    "1"

            );
        }

        // add check for username exists in a DB
        if (userRepository.existsByPhone(customerRegistrationRequest.getMobileNumber())) {
            return new CustomerRegistrationResponse(
                    "",
                    "Phone is already taken",
                    "1"

            );

        }

        // add check for email exists in DB
        if (userRepository.existsByEmail(customerRegistrationRequest.getEmail())) {
            return new CustomerRegistrationResponse(
                    "",
                    "Email is already taken",
                    "1"

            );
        }

        if (customerRegistrationRequest.getMobileNumber().isEmpty()) {
            return new CustomerRegistrationResponse(
                    "",
                    "Phone Number cannot be empty",
                    "1"

            );
        }

        if (repository.existsByEmail(customerRegistrationRequest.getEmail())) {
            return new CustomerRegistrationResponse(
                    "",
                    "Email is already taken",
                    "1"

            );
        }

        if (customerRegistrationRequest.getPin().isEmpty()) {
            return new CustomerRegistrationResponse(
                    "",
                    "Password cannot be empty",
                    "1"

            );
        }

        if (customerRegistrationRequest.getAge() == null || customerRegistrationRequest.getAge() == 0) {
            return new CustomerRegistrationResponse(
                    "",
                    "Age cannot be 0",
                    "1"

            );
        }

        // create user object
        User user = new User();
        user.setPhone(customerRegistrationRequest.getMobileNumber());
        user.setEmail(customerRegistrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(customerRegistrationRequest.getPin()));

        Role roles = roleRepository.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(roles));


        userRepository.save(user);

        Boolean existsByReferralCode = repository.existsByReferralCode(
                customerRegistrationRequest.getReferredByCode().toUpperCase()
        );


        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setEmail(customerRegistrationRequest.getEmail());
        customerEntity.setFirstName(customerRegistrationRequest.getFirstName());
        customerEntity.setLastName(customerRegistrationRequest.getLastName());
        customerEntity.setMobileNumber(phone);
        customerEntity.setAge(customerRegistrationRequest.getAge());
        customerEntity.setGender(customerRegistrationRequest.getGender());
        customerEntity.setDob(customerRegistrationRequest.getDob());
        customerEntity.setReferralCode(HelperUtility.generateReferralCode());
        customerEntity.setLocation(customerRegistrationRequest.getLocation());
        customerEntity.setDisplayName(customerRegistrationRequest.getFirstName() + " " + customerRegistrationRequest.getLastName());
        customerEntity.setIsPolicyAccepted(customerRegistrationRequest.getIsPolicyAccepted());

        if (customerRegistrationRequest.getAge() <= 25) {
            customerEntity.setAgeGroupNo(1);
        } else if (customerRegistrationRequest.getAge() <= 35) {
            customerEntity.setAgeGroupNo(2);
        } else if (customerRegistrationRequest.getAge() <= 45) {
            customerEntity.setAgeGroupNo(3);
        } else if (customerRegistrationRequest.getAge() <= 55) {
            customerEntity.setAgeGroupNo(4);
        } else {
            customerEntity.setAgeGroupNo(5);
        }

        if (Boolean.TRUE.equals(existsByReferralCode)) {
            customerEntity.setReferredByCode(customerRegistrationRequest.getReferredByCode().toUpperCase());
        }
        repository.save(customerEntity);

//        createUser();
        Random random = new Random();
        String otp = String.format("%04d", random.nextInt(10000));
        repository.saveOtp(otp, phone, customerRegistrationRequest.getEmail());

        advantaSmsApi.sendOtp(
//                "Dear "
//                        + customerRegistrationRequest.getFirstName() +
//                        "\nThank you for downloading the Teleeza App." +
//                        " Use " + otp + " as your verification code. Enjoy Freemium from ONLY 150/= monthly",
                "Thank you for downloading the Teleeza App.Use " + otp + " as verification code. Enjoy Ksh 115k Jiinue Package benefits for ONLY 349/= monthly!",
                phone
        );

        return new CustomerRegistrationResponse(
                phone,
                "User registration staged successful",
                "0"

        );
    }

    public void createUser() throws MqttException {
        // Logic to create a user
        // subscribeSample.subscribeToMQTT();
        // System.out.println();
        // Publish user created event
        publishUser.publishMessage("NewUser","New User Created ");
        // eventPublisher.publishEvent();
    }
}
