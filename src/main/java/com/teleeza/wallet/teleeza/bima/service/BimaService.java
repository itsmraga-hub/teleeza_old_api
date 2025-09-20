package com.teleeza.wallet.teleeza.bima.service;

import com.teleeza.wallet.teleeza.bima.dtos.requests.*;
import com.teleeza.wallet.teleeza.bima.dtos.requests.FamilyMembersItem;
import com.teleeza.wallet.teleeza.bima.dtos.response.*;
import com.teleeza.wallet.teleeza.bima.entities.Customer;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;

public interface BimaService {
    AddCustomerDetailsResponse addCustomerDetails(CustomerDetailsRequest customerRequest);

    Customer getCustomerDetails(String mobileNumber);

    Customer getCustomerDetailsById(Long id);

    PolicyResponse getCustomerPolicy(String mobileNumber);
    ClaimResponse makeClaim(ClaimRequest claimRequest);

    CustomerEntity getUserSubscriptionDetails(String phoneNumber);

    AddFamilyMemberResponse addAdditionalFamilyMembers(AddFamilyMemberRequest addFamilyMemberRequest);

//    CustomerResponse creatPolicy(CustomerRequest customerRequest);


}
