package com.teleeza.wallet.teleeza.bima.controller;

import com.teleeza.wallet.teleeza.bima.dtos.requests.AddFamilyMemberRequest;
import com.teleeza.wallet.teleeza.bima.dtos.requests.ClaimRequest;
import com.teleeza.wallet.teleeza.bima.dtos.requests.CustomerDetailsRequest;
import com.teleeza.wallet.teleeza.bima.dtos.requests.FamilyMembersItem;
import com.teleeza.wallet.teleeza.bima.dtos.response.AddCustomerDetailsResponse;
import com.teleeza.wallet.teleeza.bima.dtos.response.AddFamilyMemberResponse;
import com.teleeza.wallet.teleeza.bima.dtos.response.ClaimResponse;
import com.teleeza.wallet.teleeza.bima.dtos.response.PolicyResponse;
import com.teleeza.wallet.teleeza.bima.entities.Customer;
import com.teleeza.wallet.teleeza.bima.repository.FamilyMemberRepository;
import com.teleeza.wallet.teleeza.bima.repository.NextOfKinRepository;
import com.teleeza.wallet.teleeza.bima.service.BimaService;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("v1/api")
@Slf4j
public class BimaController {
    private final NextOfKinRepository nextOfKinRepository;
    private final BimaService bimaService;
    private final FamilyMemberRepository familyMemberRepository;


    public BimaController(BimaService bimaService, FamilyMemberRepository familyMemberRepository,
                          NextOfKinRepository nextOfKinRepository) {
        this.bimaService = bimaService;
        this.familyMemberRepository = familyMemberRepository;
        this.nextOfKinRepository = nextOfKinRepository;
    }

    @PostMapping("/customer")
    public ResponseEntity<AddCustomerDetailsResponse> addCustomerDetails(@RequestBody CustomerDetailsRequest customerRequest) {
        return ResponseEntity.ok(bimaService.addCustomerDetails(customerRequest));
    }

    @GetMapping("/customer")
    public ResponseEntity<Customer> getCustomerDetails(
            @RequestParam(name = "mobileNumber")String mobileNumber
    ){
        return ResponseEntity.ok(bimaService.getCustomerDetails(mobileNumber));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer> getCustomerDetailsById(
            @PathVariable(name = "id")Long id
    ){
        return ResponseEntity.ok(bimaService.getCustomerDetailsById(id));
    }

    @GetMapping("/customer/family-member-details")
        public ResponseEntity<Map<String,Object>> getFamilyMemberDetails(@RequestParam(name = "phoneNumber") String phoneNumber){
        HashMap<String,Object> response = new HashMap<>();
        Customer customer = bimaService.getCustomerDetails(phoneNumber);

        log.info("Customer : {}", customer);

        response.put("hasFamilyMebers",familyMemberRepository.existsByPrincipalPhoneNumber(phoneNumber));
        response.put("hasNextOfKin",nextOfKinRepository.existsByCustomer_Id(customer.getId()));
        response.put("membersCount", familyMemberRepository.getFamilyMembersCount(phoneNumber));

        return ResponseEntity.ok(response);

    }

    @PostMapping("/add-family-member")
    public ResponseEntity<AddFamilyMemberResponse> addFamilyMembers(@RequestBody AddFamilyMemberRequest familyMembersItem){
        return ResponseEntity.ok(bimaService.addAdditionalFamilyMembers(familyMembersItem));
    }

    @GetMapping("/customer/policy/{phoneNumber}")
    public ResponseEntity<?> getPolicy(@PathVariable(name = "phoneNumber")String phoneNumber){

        PolicyResponse response = bimaService.getCustomerPolicy(phoneNumber);
        HashMap<String,Object> newResponse = new HashMap<>();
        newResponse.put("policyNumber",response.getPolicyNumber());
        newResponse.put("status",response.getStatus());
        newResponse.put("policyStartDate",response.getStartDate());
        newResponse.put("policyEndDate",response.getEndDate());
        newResponse.put("coverStartDate",response.getCoverPeriods().get(0).getStartDate());
        newResponse.put("coverEndDate",response.getCoverPeriods().get(0).getEndDate());
        return ResponseEntity.ok(newResponse);
    }

    @PostMapping(path = "/make-claim", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ClaimResponse> makeClaim(@RequestBody ClaimRequest request) {
        // Call the API service to make the claim
        ClaimResponse claimResponse = bimaService.makeClaim(request);

        // Check if the claim response is not null
        if (claimResponse != null) {
            // Return the claim response
            return ResponseEntity.ok(claimResponse);
        } else {
            // Handle the case when the claim response is empty
            return ResponseEntity.noContent().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_PARTNER_TURACO')")
    @GetMapping("/customer/details/{phoneNumber}")
    public ResponseEntity<?> getUserDetails(@PathVariable(name = "phoneNumber")String phoneNumber){
        CustomerEntity customer = bimaService.getUserSubscriptionDetails(phoneNumber);
        HashMap<String,Object> newResponse = new HashMap<>();
        newResponse.put("firstName", customer.getFirstName());
        newResponse.put("lastName",customer.getLastName());
        newResponse.put("surname", customer.getMiddleName());
        newResponse.put("isScubscribed", customer.getIsSubscribed());
        newResponse.put("expiryDate",customer.getExpirationTime().toString());
        return ResponseEntity.ok(newResponse);
    }
}
