package com.teleeza.wallet.teleeza.customer_registration.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests.*;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.CustomerResponse;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.FcmResponseDto;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.KycUpdateResponse;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.customer_registration.service.CustomerRegistrationService;
import com.teleeza.wallet.teleeza.daraja.entity.Commissions;
import com.teleeza.wallet.teleeza.daraja.repository.CommissionsRepository;
import com.teleeza.wallet.teleeza.daraja.repository.MpesaTransactionsRepository;
import com.teleeza.wallet.teleeza.daraja.service.DarajaApi;
import com.teleeza.wallet.teleeza.daraja.service.DarajaApiImpl;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.service.TransactionsService;
import com.teleeza.wallet.teleeza.utils.ResponseHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.RoundingMode;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/v1/teleeza-wallet")
@Slf4j
@RequiredArgsConstructor
public class CustomerRegistrationController {
    private final SasaPayApi sasapayApi;
    private final CustomerRegistrationRepository repository;
    private final RestTemplate restTemplate;

//    @Value("${app.jwt-secret}")
//    private String  jwtSigningKey;

//    private static final Key SECRET_KEY = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);


    private final HttpHeaders httpHeaders;

    private final TransactionsService transactionsService;
    private final CustomerRegistrationService customerRegistrationService;
    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final CommissionsRepository commissionsRepository;
    private final AdvantaSmsApiImpl advantaSmsApi;
    private final DarajaApi darajaApi;
    private final DarajaApiImpl darajaApiImpl;
    private final MpesaTransactionsRepository mpesaTransactionsRepository;


    @PostMapping(path = "/register-customer", produces = "application/json")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) throws MqttException {
        return new ResponseEntity<>(customerRegistrationService.registerCustomer(customerRegistrationRequest), HttpStatus.OK);
    }

    @PostMapping(path = "/customer-confirmation", produces = "application/json")
    public ResponseEntity<?> customerConfirmation(@RequestBody CustomerConfirmationRequest customerConfirmationRequest) {
        Optional<CustomerEntity> customer = repository.findByMobileNumber(customerConfirmationRequest.getRegistrationRequestId());
        Commissions commissions = commissionsRepository.findByCategory("subscriptions");

        if (customer.isPresent()) {
            CustomerEntity referringCustomer = repository.findUserByReferralCode(customer.get().getReferredByCode());
            boolean isTelkomNumber = customer.get().getMobileNumber().startsWith("+25477"); //
//            boolean isReferredByWeruTv = false;
//            boolean isReferredByReferee = false;
            if (customerConfirmationRequest.getConfirmationCode().equals(customer.get().getOtp())
                    && customer.get().getMobileVerified().equals(false)) {
                if (referringCustomer != null) {
                    Random random = new Random();
                    String otp = String.format("%04d", random.nextInt(10000));
                    customer.get().setMobileVerified(true);
                    customer.get().setUpdatedOn(LocalDateTime.now());
                    customer.get().setOtp(otp);
                    repository.save(customer.get());

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("message", "User confirmation successful");
                    map.put("statusCode", "0");
                    return ResponseHandler.customerConfirmationResponse(HttpStatus.OK, map);

                } else {

                    customer.get().setMobileVerified(true);
                    customer.get().setUpdatedOn(LocalDateTime.now());
                    repository.save(customer.get());

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("message", "User confirmation successful");
                    map.put("statusCode", "0");

                    return ResponseHandler.customerConfirmationResponse(HttpStatus.OK, map);
                }

            } else {
                // OTP DON't Match
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("message", "User confirmation failed. Account already verified");
                map.put("statusCode", "1");
                return ResponseHandler.customerConfirmationResponse(HttpStatus.OK, map);
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", "User confirmation failed.");
        map.put("statusCode", "1");
        return ResponseHandler.customerConfirmationResponse(HttpStatus.OK, map);
    }

    @PostMapping("/send-commission")
    public ResponseEntity<?> sendReferralCommission(@RequestBody ReferralCommissionRequest request) {
        Optional<CustomerEntity> customer = repository.findByMobileNumber(request.getPhoneNumber());
        Commissions commissions = commissionsRepository.findByCategory("subscriptions");

        if (customer.isPresent() && customer.get().getMobileVerified().equals(true)) {
            CustomerEntity referringCustomer = repository.findUserByReferralCode(customer.get().getReferredByCode());
            boolean isTelkomNumber = customer.get().getMobileNumber().startsWith("+25477"); //
            if (referringCustomer != null && referringCustomer.getMobileVerified().equals(true)
                    && customer.get().getIsReferrerPaid().equals(false)
            ) {
                log.info("Referrer : {}", referringCustomer);
                log.info("Customer : {}", customer.get().getIsReferrerPaid());
                log.info("IsTelkom : {}", !isTelkomNumber);
                log.info("Locked : {}", referringCustomer.getLocked());
                if (referringCustomer.getMobileUserType().equals("Super Agent")
                        && !isTelkomNumber
                        && referringCustomer.getLocked().equals(false)
                ) {
                    darajaApi.sendReferralCommission(referringCustomer.getMobileNumber().substring(1), commissions.getSuperAgentAmount());
                }

                if (referringCustomer.getMobileUserType().equals("Agent")
                        && !isTelkomNumber
                        && referringCustomer.getLocked().equals(false)
                       // && customer.get().getIsReferrerPaid().equals(false)
                ) {

                    log.info("Locked : {}", referringCustomer.getLocked());
                    darajaApiImpl.sendReferralCommission(referringCustomer.getMobileNumber().substring(1), commissions.getAgentAmount());
                }


                if (referringCustomer.getMobileUserType().equals("Coach") && !isTelkomNumber
                        && referringCustomer.getLocked().equals(false)
                    //    && customer.get().getIsReferrerPaid().equals(false)
                ) {
                    log.info("Send Commission ");
                    darajaApi.sendReferralCommission(referringCustomer.getMobileNumber().substring(1), commissions.getCoachAmount());
                }

                customer.get().setMobileVerified(true);
                customer.get().setUpdatedOn(LocalDateTime.now());
                customer.get().setIsReferrerPaid(true);
                repository.save(customer.get());

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("message", "Referrer credited");
                map.put("statusCode", "0");
                return ResponseHandler.customerConfirmationResponse(HttpStatus.OK, map);
            }else {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("message", "Referrer already paid");
                map.put("statusCode", "1");
                return ResponseHandler.customerConfirmationResponse(HttpStatus.OK, map);
            }

        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", "Unable to verify user details.");
        map.put("statusCode", "1");
        return ResponseHandler.customerConfirmationResponse(HttpStatus.OK, map);

    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ResendOtpRequest resendOtpRequest) {
        Optional<CustomerEntity> customer = repository.findByMobileNumber(resendOtpRequest.getPhoneNumber());
        if (customer.isPresent() && customer.get().getMobileVerified().equals(false)) {
            Random random = new Random();
            String otp = String.format("%04d", random.nextInt(10000));
            repository.saveOtp(otp, resendOtpRequest.getPhoneNumber(), resendOtpRequest.getEmail());

            advantaSmsApi.sendOtp("Dear " + customer.get().getFirstName() + "\nThank you for downloading the Teleeza App." + " Use " + otp + " as your verification code. Enjoy Freemium from ONLY 150/= monthly", resendOtpRequest.getPhoneNumber());


            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", "Otp sent");
            map.put("statusCode", "0");
            return ResponseHandler.generateResponse(HttpStatus.OK, map);
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", "Otp not sent. Invalid operation");
            map.put("statusCode", "1");
            return ResponseHandler.generateResponse(HttpStatus.OK, map);
        }
    }


    @PostMapping("/update-kyc")
    public ResponseEntity<KycUpdateResponse> updateUserKyc(@RequestBody KycUpdateRequest kycUpdateRequest) {

        KycUpdateResponse kycUpdateResponse = sasapayApi.updateUserKyc(kycUpdateRequest);
        return ResponseEntity.ok(kycUpdateResponse);
    }

    @GetMapping(path = "/referrals")
    public Map<String, Object> getReferrals(String code, String phone) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("referrals", customerRegistrationService.getUsersReferrals(code)); // pass my referral code
        response.put("totalReferrals", customerRegistrationRepository.countAllByReferredByCode(code));
        response.put("totalEarned", mpesaTransactionsRepository.totalAmountEarned(phone.substring(1)).setScale(2, RoundingMode.HALF_EVEN));
        return response;
    }


    @GetMapping(path = "/user-income")
    public Map<String, List<Transactions>> getUsersIncome(String beneficiaryAccNumber) {
        Map<String, List<Transactions>> response = new HashMap<>();
        response.put("transactions", customerRegistrationService.getAllUserIncome(beneficiaryAccNumber));
        return response;
    }

    @GetMapping(path = "/user-expenses")
    public Map<String, List<Transactions>> getUsersExpenses(String beneficiarryAccountNumber) {
        Map<String, List<Transactions>> response = new HashMap<>();
        response.put("transactions", customerRegistrationService.getAllUserExpenses(beneficiarryAccountNumber));
        return response;
    }

    @GetMapping("/customer-details")
    public ResponseEntity<Object> getCustomerDetails(@RequestParam String CustomerAccountNumber) {

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(sasapayApi.getAccessToken().getAccessToken());

        String url = String.format("%s?CustomerAccountNumber=%s", "https://api.sasapay.app//api/v1/waas/customers/detail/", CustomerAccountNumber);

//        String url = "https://sandbox.sasapay.app//api/v1/waas/customers/detail/?CustomerAccountNumber=Cus";

        // build the request
        HttpEntity request = new HttpEntity(httpHeaders);

        // make an HTTP GET request with headers
        ResponseEntity<CustomerResponse> response = restTemplate.exchange(url, HttpMethod.GET, request, CustomerResponse.class

        );

        // check response
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful.");
            System.out.println(response.getBody());

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("DisplayName", Objects.requireNonNull(response.getBody()).getResult().getCustomer().getDisplayName());
            map.put("CustomerBalance", response.getBody().getResult().getCustomerBalance());
            map.put("statusCode", response.getBody().getStatusCode());
            map.put("accountStatus", response.getBody().getResult().getStatus());
            map.put("transactions", transactionsService.recentTransactions(CustomerAccountNumber));
            map.put("todaysTopup", transactionsService.sumOfTodaysIncome(response.getBody().getResult().getCustomerAccountNumber()));
            map.put("todaysExpense", transactionsService.sumOfTodaysExpense(response.getBody().getResult().getCustomerAccountNumber()));
            return ResponseHandler.generateResponse(HttpStatus.OK, map);
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
            return null;
        }
    }

    @GetMapping("/registered-customer")
    public ResponseEntity<Object> checkCustomerRegistration(
            @RequestHeader(value = "Authorization") String authorizationHeader
//            @RequestParam(value = "mobile_number") String mobileNumber
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        DecodedJWT decodedJWT = JWT.decode(token);
//        log.info("Decoded JWT: {}", decodedJWT.getClaims());
//        log.info("Decoded JWT: {}", decodedJWT);
//        log.info("Decoded JWT: {}", decodedJWT.getSubject());
        // Extract the phone number from the claims
        String phoneNumberFromToken = decodedJWT.getClaim("phone_number").asString();

//        System.out.println("JWT token: " + token);
//        System.out.println("Phone number from token: " + phoneNumberFromToken);
        // Use the phone number from the token or the one provided in the request
//        String phone = phoneNumberFromToken != null ? phoneNumberFromToken : "+254" + mobileNumber.substring(1);
        String phone = phoneNumberFromToken != null ? phoneNumberFromToken : "+254" + decodedJWT.getSubject().substring(1);
//        String phone = "+254" + mobileNumber.substring(1);
        Object response = customerRegistrationService.getRegisteredCustomerDetails(phone);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomerEntity(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), HttpStatus.OK);
    }

//    @GetMapping("/registered-customer")
//    public ResponseEntity<Object> checkCustomerRegistration(
//            @RequestParam(value = "mobile_number") String mobileNumber,
//            @RequestHeader("Authorization") String authorizationHeader) { // Access the Authorization header
//
//        // Extract the JWT token from the Authorization header
//        String token = authorizationHeader.replace("Bearer ", "");
//        System.out.println("JWT token: " + token);
//        // Parse the JWT token to extract claims (e.g., phone number)
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(SECRET_KEY) // Replace with your secret key
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        log.info("Claims: {}", claims);
//        // Extract the phone number from the JWT claims (if needed)
//        String phoneNumberFromToken = claims.get("phone_number", String.class);
//        System.out.println("Phone number from token: " + phoneNumberFromToken);
//
//
//        // Process the mobile number from the request parameter
//        String phone = "+254" + mobileNumber.substring(1);
//
//        // Call your service method
//        Object response = customerRegistrationService.getRegisteredCustomerDetails(phone);
//
//        // Return the response
//        if (response != null) {
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        }
//
//        return new ResponseEntity<>(new CustomerEntity(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), HttpStatus.OK);
//    }

    @PostMapping("/fcm-token")
    public ResponseEntity<?> saveUserFcmToken(@RequestBody FcmRequestDto fcmRequestDto) {
        CustomerEntity customer = repository.findCustomerByPhoneNumber(fcmRequestDto.getBeneficiaryAccNo());
        customer.setFcmToken(fcmRequestDto.getFcmToken());
        repository.save(customer);
        return new ResponseEntity<>(new FcmResponseDto(fcmRequestDto.getBeneficiaryAccNo(), fcmRequestDto.getFcmToken(), "Fcm Saved successful"), HttpStatus.OK);
    }
}
