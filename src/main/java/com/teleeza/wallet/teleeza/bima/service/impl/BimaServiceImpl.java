package com.teleeza.wallet.teleeza.bima.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.bima.config.BimaConfig;
import com.teleeza.wallet.teleeza.bima.dtos.requests.*;
import com.teleeza.wallet.teleeza.bima.dtos.requests.FamilyMembersItem;
import com.teleeza.wallet.teleeza.bima.dtos.requests.NextOfKinItem;
import com.teleeza.wallet.teleeza.bima.dtos.response.*;
import com.teleeza.wallet.teleeza.bima.entities.Customer;
import com.teleeza.wallet.teleeza.bima.entities.FamilyMember;
import com.teleeza.wallet.teleeza.bima.entities.NextOfKin;
import com.teleeza.wallet.teleeza.bima.repository.CustomerRepository;
import com.teleeza.wallet.teleeza.bima.repository.FamilyMemberRepository;
import com.teleeza.wallet.teleeza.bima.repository.NextOfKinRepository;
import com.teleeza.wallet.teleeza.bima.service.BimaService;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.daraja.entity.MpesaTransactions;
import com.teleeza.wallet.teleeza.daraja.repository.MpesaTransactionsRepository;
import com.teleeza.wallet.teleeza.daraja.stk.dtos.reponse.StkPushAsyncResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Slf4j
@Service
public class BimaServiceImpl implements BimaService {
    private final CustomerRepository customerRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final NextOfKinRepository nextOfKinRepository;
    private final HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BimaConfig bimaConfig;
    private final MpesaTransactionsRepository mpesaTransactionsRepository;
    private final CustomerRegistrationRepository customerRegistrationRepository;

    public BimaServiceImpl(CustomerRepository customerRepository,
                           FamilyMemberRepository familyMemberRepository,
                           NextOfKinRepository nextOfKinRepository, HttpHeaders httpHeaders, RestTemplate restTemplate,
                           ObjectMapper objectMapper, BimaConfig bimaConfig,
                           MpesaTransactionsRepository mpesaTransactionsRepository,
                           CustomerRegistrationRepository customerRegistrationRepository) {
        this.customerRepository = customerRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.nextOfKinRepository = nextOfKinRepository;
        this.httpHeaders = httpHeaders;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.bimaConfig = bimaConfig;
        this.mpesaTransactionsRepository = mpesaTransactionsRepository;
        this.customerRegistrationRepository = customerRegistrationRepository;
    }

    @Override
    public AddCustomerDetailsResponse addCustomerDetails(CustomerDetailsRequest customerRequest) {
        // check if customer already exists
        Boolean customerExists = customerRepository.existsByMobileNumberOrIdNumber(
                customerRequest.getCustomer().getMobileNumber(), customerRequest.getCustomer().getIdNumber()
        );

        if (customerExists) {
            Customer existingCustomer = customerRepository.findCustomerByMobileNumberOrIdNumber(
                    customerRequest.getCustomer().getMobileNumber(), customerRequest.getCustomer().getIdNumber()
            );
            NextOfKin nextOfKin = customerRequest.getNextOfKin();
            nextOfKinRepository.save(nextOfKin);

            // Save Family Members
            List<FamilyMember> familyMembers = customerRequest.getFamilyMember();
            for (FamilyMember familyMember : familyMembers) {
                familyMember.setCustomer(existingCustomer); // Set the Customer object to the FamilyMember
                familyMemberRepository.save(familyMember);
            }

            return new AddCustomerDetailsResponse("Customer already exists");
        } else {
            // Create and save Customer entity
            Customer customer = customerRequest.getCustomer();
            log.info("Customer :{}", customer);
            customerRepository.save(customer);

            // Save Family Members
            List<FamilyMember> familyMembers = customerRequest.getFamilyMember();
            for (FamilyMember familyMember : familyMembers) {
                familyMember.setCustomer(customer); // Set the Customer object to the FamilyMember
                familyMemberRepository.save(familyMember);
            }

            // Save Next of Kin
            NextOfKin nextOfKin = customerRequest.getNextOfKin();
            log.info("Next Of Kin : {}", nextOfKin);
            nextOfKin.setCustomer(customer);
            nextOfKinRepository.save(nextOfKin);

            return new AddCustomerDetailsResponse("Customer details created successfully");
        }
    }

    @Override
    public Customer getCustomerDetails(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber);
        log.info("Customer Phone : {}", mobileNumber);
        log.info("Customer : {}", customer);

        return customer;
    }

    @Override
    public Customer getCustomerDetailsById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        log.info("Customer Id: {}", customer);
        return customer.get();
    }

    @Override
    public PolicyResponse getCustomerPolicy(String mobileNumber) {

        httpHeaders.setBearerAuth(bimaConfig.getToken());

        Customer customer = customerRepository.findByMobileNumber(mobileNumber);
        HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                bimaConfig.getBimaGetPolicyEndpoint() + "/" + customer.getPolicyId(),
                HttpMethod.GET,
                entity, PolicyResponse.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return response.getBody();
        }
    }

    @Override
    public ClaimResponse makeClaim(ClaimRequest claimRequest) {
        httpHeaders.setBearerAuth(bimaConfig.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        claimRequest.setProductId(28607652);

        HttpEntity<ClaimRequest> requestEntity = new HttpEntity<>(claimRequest, httpHeaders);
        String url = "https://test.turaco.insure/api/v1/claims";

        ClaimResponse response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ClaimResponse.class
        ).getBody();
        return response;
    }

    @Override
    public CustomerEntity getUserSubscriptionDetails(String phoneNumber) {
        CustomerEntity customer = customerRegistrationRepository.getUserSubscriptionDetails("+" + phoneNumber);
        log.info("Customer : {}", customer.getFirstName());
        return customerRegistrationRepository.getUserSubscriptionDetails("+" + phoneNumber);
    }

    @Override
    public AddFamilyMemberResponse addAdditionalFamilyMembers(AddFamilyMemberRequest addFamilyMemberRequest) {
        Customer   customer =  customerRepository.findByMobileNumber(addFamilyMemberRequest.getPrincipalPhoneNumber());
        if(customer != null){

            // Save Family Members
            List<FamilyMember> familyMembers = addFamilyMemberRequest.getFamilyMembers();
            for (FamilyMember familyMember : familyMembers) {
                familyMember.setCustomer(customer); // Set the Customer object to the FamilyMember
                familyMemberRepository.save(familyMember);
            }
//            FamilyMember familyMember = new FamilyMember();
//            familyMember.setFullName(addFamilyMemberRequest.getFamilyMembers());
//            familyMember.setRelationship(familyMembersItem.getRelationship());
//            familyMember.setMobileNumber(familyMembersItem.getMobileNumber());
//            familyMember.setGender(familyMembersItem.getGender());
//            familyMember.setDateOfBirth(familyMembersItem.getDateOfBirth());
//            familyMember.setEmail(familyMembersItem.getEmail());
//            familyMember.setAge(familyMembersItem.getAge());
//            familyMember.setPrincipalPhoneNumber(familyMembersItem.getPrincipalPhoneNumber());
//            familyMember.setCustomer(customer);
//            familyMemberRepository.save(familyMember);

            return new AddFamilyMemberResponse("Family member added successfully","0");
        }else {
            return new AddFamilyMemberResponse("Bima customer does not exist","0");
        }

    }

    //    @Override
    @Transactional
    public String creatPolicy(StkPushAsyncResponse stkPushAsyncResponse) throws JsonProcessingException {

        MpesaTransactions transactions = mpesaTransactionsRepository.findByMerchantRequestId(stkPushAsyncResponse.getBody().getStkCallback().getMerchantRequestID());

        CustomerEntity subscribingCustomer = customerRegistrationRepository.findCustomerByPhoneNumber(transactions.getAccountReference());

        log.info("Subscribing Customer : {}", subscribingCustomer);

        String customerPhone = subscribingCustomer.getMobileNumber();
        String phone = "" + customerPhone.substring(1);


        Customer bimaCustomer = customerRepository.getCustomerByMobile(subscribingCustomer.getMobileNumber().substring(1));
        log.info("Bima Customer ID : {}", bimaCustomer);
        NextOfKin bimaCustomerNextOfKin = nextOfKinRepository.getNextOfKinByCustomerId(bimaCustomer.getId());
        List<FamilyMember> familyMember = familyMemberRepository.getFamilyMemberByCustomerId(bimaCustomer.getId());


        // get customer details from 'customer' table and create policy
        log.info("Bima Customer Info : {}", bimaCustomer);
        log.info("Bima Customer Next Of Kin : {}", bimaCustomerNextOfKin);
        log.info("Bima Customer Family Members : {}", familyMember);
//
        CustomerItem customer = new CustomerItem();
        customer.setFullName(bimaCustomer.getFullName());
        customer.setMobileNumber(bimaCustomer.getMobileNumber());
        customer.setAddress(bimaCustomer.getAddress());
        customer.setGender(bimaCustomer.getGender().toUpperCase());
        customer.setDateOfBirth(bimaCustomer.getDateOfBirth());
        customer.setEmail(bimaCustomer.getEmail());
        customer.setIdNumber(bimaCustomer.getIdNumber());
        customer.setExternalRef(bimaConfig.getExternalRef());
        customer.setDocumentType(bimaCustomer.getDocumentType());

        List<FamilyMembersItem> familyMembers = new ArrayList<>();
        for (FamilyMember fetchedFamilyMember : familyMember) {
            FamilyMembersItem member = new FamilyMembersItem();
            member.setFullName(fetchedFamilyMember.getFullName());
            member.setRelationship(fetchedFamilyMember.getRelationship().toUpperCase());
            member.setMobileNumber(fetchedFamilyMember.getMobileNumber());
            member.setGender(fetchedFamilyMember.getGender().toUpperCase());
            member.setDateOfBirth(fetchedFamilyMember.getDateOfBirth());
            member.setEmail(fetchedFamilyMember.getEmail());

            familyMembers.add(member);
        }

        NextOfKinItem nextOfKinItem = new NextOfKinItem();
        nextOfKinItem.setFullName(bimaCustomer.getFullName());
        nextOfKinItem.setRelationship(bimaCustomerNextOfKin.getRelationship().toUpperCase());
        nextOfKinItem.setMobileNumber(bimaCustomerNextOfKin.getMobileNumber());
        nextOfKinItem.setGender(bimaCustomerNextOfKin.getGender().toUpperCase());
        nextOfKinItem.setDateOfBirth(bimaCustomerNextOfKin.getDateOfBirth());
        nextOfKinItem.setEmail(bimaCustomerNextOfKin.getEmail());
        nextOfKinItem.setAge(bimaCustomerNextOfKin.getAge());

        // Calculate the first day of the next month
        LocalDate firstDayOfNextMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDateOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setCustomer(customer);
        customerRequest.setFamilyMembers(familyMembers);
        customerRequest.setNextOfKin(nextOfKinItem);
        if (currentDate.equals(firstDateOfMonth)) {
            customerRequest.setStartDate(currentDate.toString());
            customerRequest.setEndDate(currentDate.with(TemporalAdjusters.lastDayOfMonth()).toString());
        } else {
            customerRequest.setStartDate(firstDayOfNextMonth.toString());
            customerRequest.setEndDate(firstDayOfNextMonth.with(TemporalAdjusters.lastDayOfMonth()).toString());
        }
        customerRequest.setProductPackId(bimaConfig.getProductPackId());
        customerRequest.setStartDate(firstDayOfNextMonth.toString());

        if (stkPushAsyncResponse.getBody().getStkCallback().getResultCode() == 0) {

            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setBearerAuth(bimaConfig.getToken());
            httpHeaders.setCacheControl("private, no-store, max-age=0");
            httpHeaders.setExpires(0);

            Map<String, Object> bimaRequest = new HashMap<>();
            bimaRequest.put("customer", customerRequest.getCustomer());
            bimaRequest.put("familyMembers", customerRequest.getFamilyMembers());
            bimaRequest.put("nextOfKin", customerRequest.getNextOfKin());
            if (currentDate.equals(firstDateOfMonth)) {
                bimaRequest.put("startDate", currentDate.toString());
                bimaRequest.put("endDate", currentDate.with(TemporalAdjusters.lastDayOfMonth()).toString());
            } else {
                bimaRequest.put("startDate", firstDayOfNextMonth.toString());
                bimaRequest.put("endDate", firstDayOfNextMonth.with(TemporalAdjusters.lastDayOfMonth()).toString());
            }

            bimaRequest.put("productPackId", bimaConfig.getProductPackId());

            log.info("Request Body : {}", customerRequest);

            HttpEntity<?> request = new HttpEntity<>(bimaRequest, httpHeaders);
            log.info("Request : {}", request.getBody());
            log.info("Url : {}", bimaConfig.getBimaEndpoint());

          try {
              ResponseEntity<CustomerResponse> createPolicyResponse = restTemplate.postForEntity(
                      bimaConfig.getBimaEndpoint(),
                      request, CustomerResponse.class);
              if (createPolicyResponse.getStatusCode() == HttpStatus.CREATED) {
                  log.info("Policy Response : {}", objectMapper.writeValueAsString(createPolicyResponse.getBody()));
                  if(Objects.requireNonNull(createPolicyResponse.getBody()).getCustomerMobileNumber().startsWith("+")){
                      Customer customerEntity = customerRepository.findByMobileNumber(createPolicyResponse.getBody().getCustomerMobileNumber().substring(1));
                      log.info("Customer : {}", customerEntity);
                      customerEntity.setPolicyId(Objects.requireNonNull(createPolicyResponse.getBody()).getId());
                      customerEntity.setPolicyNumber(createPolicyResponse.getBody().getPolicyNumber());
                      customerEntity.setTotalPayments(BigDecimal.valueOf(createPolicyResponse.getBody().getTotalPayments()));
                      customerEntity.setStartDate(createPolicyResponse.getBody().getStartDate());
                      customerEntity.setEndDate(createPolicyResponse.getBody().getEndDate());
                      customerRepository.save(customerEntity);
                  }else {
                      Customer customerEntity = customerRepository.findByMobileNumber(createPolicyResponse.getBody().getCustomerMobileNumber());
                      log.info("Customer : {}", customerEntity);
                      customerEntity.setPolicyId(Objects.requireNonNull(createPolicyResponse.getBody()).getId());
                      customerEntity.setPolicyNumber(createPolicyResponse.getBody().getPolicyNumber());
                      customerEntity.setTotalPayments(BigDecimal.valueOf(createPolicyResponse.getBody().getTotalPayments()));
                      customerEntity.setStartDate(createPolicyResponse.getBody().getStartDate());
                      customerEntity.setEndDate(createPolicyResponse.getBody().getEndDate());
                      customerRepository.save(customerEntity);
                  }

                  return "";
//                return Objects.requireNonNull(createPolicyResponse.getBody()).toString();
              } else if (createPolicyResponse.getStatusCode()==HttpStatus.OK) {
                  log.info("Policy Response : {}", objectMapper.writeValueAsString(createPolicyResponse.getBody()));
                  if(Objects.requireNonNull(createPolicyResponse.getBody()).getCustomerMobileNumber().startsWith("+")){
                      Customer customerEntity = customerRepository.findByMobileNumber(createPolicyResponse.getBody().getCustomerMobileNumber().substring(1));
                      log.info("Customer : {}", customerEntity);
                      customerEntity.setPolicyId(Objects.requireNonNull(createPolicyResponse.getBody()).getId());
                      customerEntity.setPolicyNumber(createPolicyResponse.getBody().getPolicyNumber());
                      customerEntity.setTotalPayments(BigDecimal.valueOf(createPolicyResponse.getBody().getTotalPayments()));
                      customerEntity.setStartDate(createPolicyResponse.getBody().getStartDate());
                      customerEntity.setEndDate(createPolicyResponse.getBody().getEndDate());
                      customerRepository.save(customerEntity);
                  }else {
                      Customer customerEntity = customerRepository.findByMobileNumber(createPolicyResponse.getBody().getCustomerMobileNumber());
                      log.info("Customer : {}", customerEntity);
                      customerEntity.setPolicyId(Objects.requireNonNull(createPolicyResponse.getBody()).getId());
                      customerEntity.setPolicyNumber(createPolicyResponse.getBody().getPolicyNumber());
                      customerEntity.setTotalPayments(BigDecimal.valueOf(createPolicyResponse.getBody().getTotalPayments()));
                      customerEntity.setStartDate(createPolicyResponse.getBody().getStartDate());
                      customerEntity.setEndDate(createPolicyResponse.getBody().getEndDate());
                      customerRepository.save(customerEntity);
                  }

              } else if (createPolicyResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                  log.info("ERROR : {}", createPolicyResponse.getBody());
                  handleErrorResponse(createPolicyResponse.getStatusCode(),createPolicyResponse.getBody());
                  return "";

              } else if (createPolicyResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                  handleErrorResponse(createPolicyResponse.getStatusCode(),createPolicyResponse.getBody());
                  log.info("Bad Request. Policy details already exists : {}" , createPolicyResponse.getBody());
              } else {
                  log.info("Policy Response : {}", createPolicyResponse.getBody());
                  Customer customerEntity = customerRepository.findByMobileNumber(Objects.requireNonNull(createPolicyResponse.getBody()).getCustomerMobileNumber());
                  log.info("Customer : {}", customerEntity);
                  customerEntity.setPolicyId(Objects.requireNonNull(createPolicyResponse.getBody()).getId());
                  customerEntity.setPolicyNumber(createPolicyResponse.getBody().getPolicyNumber());
                  customerEntity.setTotalPayments(BigDecimal.valueOf(createPolicyResponse.getBody().getTotalPayments()));
                  customerEntity.setStartDate(createPolicyResponse.getBody().getStartDate());
                  customerEntity.setEndDate(createPolicyResponse.getBody().getEndDate());
                  customerRepository.save(customerEntity);
                  log.info(" ERROR Policy Response : {}", createPolicyResponse.getBody());

                  handleErrorResponse(createPolicyResponse.getStatusCode(),createPolicyResponse.getBody());
                  return "";

              }

          }catch (HttpStatusCodeException exception){
              handleErrorResponse(exception.getStatusCode(),null);
          }
          catch (Exception e){
              handleOtherException(e);
              return "";
          }
        }
        return null;
    }

    private void handleErrorResponse(HttpStatus statusCode,CustomerResponse response) throws JsonProcessingException {
        if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
            // Handle internal server error
            System.out.println("Internal server error occurred.");
            System.out.println("Response"+ objectMapper.writeValueAsString(response));
          //  throw new RuntimeException("Internal Server Error occured");
        } else if (statusCode == HttpStatus.BAD_REQUEST) {
            // Handle bad request
            System.out.println("Bad request error occurred. " + objectMapper.writeValueAsString(response));
            //throw new RuntimeException("Bad request");

        } else {
            // Handle other HTTP errors
            System.out.println("HTTP error occurred: " + statusCode);
            //throw new RuntimeException("Error occured");
        }
    }

    private void handleOtherException(Exception e) {
        // Handle other exceptions (e.g., network error)
        System.out.println("Exception occurred: " + e.getMessage());
     //   throw new RuntimeException("Exception occured"+ e.getMessage());
    }


}
