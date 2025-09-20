package com.teleeza.wallet.teleeza.authentication.teleeza.service;

import com.teleeza.wallet.teleeza.advanta.service.AdvantaSmsApiImpl;
import com.teleeza.wallet.teleeza.authentication.teleeza.entity.Role;
import com.teleeza.wallet.teleeza.authentication.teleeza.entity.User;
import com.teleeza.wallet.teleeza.authentication.teleeza.payload.*;
import com.teleeza.wallet.teleeza.authentication.teleeza.repository.AuthRepository;
import com.teleeza.wallet.teleeza.authentication.teleeza.repository.RoleRepository;
import com.teleeza.wallet.teleeza.authentication.teleeza.security.JwtTokenProvider;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.customer_registration.service.CustomerRegistrationService;
import com.teleeza.wallet.teleeza.notification.model.dtos.requests.PushNotificationRequest;
import com.teleeza.wallet.teleeza.notification.service.PushNotificationService;
import com.teleeza.wallet.teleeza.rewarded_ads.merchant.repository.MerchantRepository;
import com.teleeza.wallet.teleeza.rewarded_ads.service.SendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private AdvantaSmsApiImpl advantaSmsApiImpl;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private CustomerRegistrationService customerRegistrationService;

    @Autowired
    private MerchantRepository merchantRepository;

    @Transactional
    public ResponseEntity<?> authenticate(LoginDto loginDto) {
        String mobile = "+254" + loginDto.getPhone().substring(1);
        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(mobile);
        Optional<User> user = userRepository.findByPhone(loginDto.getPhone());
        if (user.isPresent()) {
            if (user.get().isAccountNonLocked()) {
                return new ResponseEntity<>(
                        new JWTAuthResponse(
                                null,
                                true,
                                "Your Account has been blocked. Please contact support.",
                                user.get().getEmail(),
                                user.get().getStatus(),
                                merchantRepository.existsByPhone(loginDto.getPhone()),
                                null
                        ),
                        HttpStatus.OK);
            } else {
                if (passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
//                    if(loginDto.getFirebaseToken())
                    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            loginDto.getPhone(), loginDto.getPassword()));


                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // get token form tokenProvider
                    String token = tokenProvider.generateToken(authentication);
                    Boolean expried = tokenProvider.validateToken(token);

                    if (customer != null) {
                        CustomerEntity customerEntity = customerRegistrationService.getRegisteredCustomerDetails(mobile);
                        if (loginDto.getFirebaseToken().equals(customer.getFcmToken())) {
                            user.get().setFailedAttempt(0);

                            userRepository.save(user.get());
                            return ResponseEntity.ok(new JWTAuthResponse(
                                    token,
                                    false,
                                    "Login Success",
                                    user.get().getEmail(),
                                    user.get().getStatus(),
                                    merchantRepository.existsByPhone(loginDto.getPhone()),
                                    customerEntity
                            ));
                        } else {
                            // send push notification to recipient
                            PushNotificationRequest request = new PushNotificationRequest();
                            request.setTitle("Logged Out");
                            request.setMessage("Hi " + customer.getFirstName() + " , We have detected a recent login into your account from another device.");
                            request.setToken(customer.getFcmToken());
                            request.setTopic("Authentication");
                            pushNotificationService.sendPushNotificationToRecipientToken(request);

                            customer.setFcmToken(loginDto.getFirebaseToken());
                            customerRegistrationRepository.save(customer);

                            user.get().setFailedAttempt(0);
                            userRepository.save(user.get());
                            return ResponseEntity.ok(new
                                    JWTAuthResponse(
                                    token,
                                    false,
                                    "Login Success",
                                    user.get().getEmail(),
                                    user.get().getStatus(),
                                    merchantRepository.existsByPhone(loginDto.getPhone()),
                                    customerEntity
                            ));
                        }
                    } else {
                        CustomerEntity customerEntity = customerRegistrationService.getRegisteredCustomerDetails(mobile);
                        return ResponseEntity.ok(new JWTAuthResponse(
                                token,
                                false,
                                "Login Success",
                                user.get().getEmail(),
                                user.get().getStatus(),
                                merchantRepository.existsByPhone(loginDto.getPhone()),
                                customerEntity
                        ));
                    }

                } else {
                    if (user.get().getFailedAttempt() < 4) {
                        user.get().setFailedAttempt(user.get().getFailedAttempt() + 1);
                        int remainingAttempts = 4 - user.get().getFailedAttempt();
                        userRepository.save(user.get());
                        return new ResponseEntity<>(
                                new JWTAuthResponse(
                                        null,
                                        true,
                                        "Invalid PIN, " + remainingAttempts + " Attempts Remaining",
                                        user.get().getEmail(),
                                        user.get().getStatus(),
                                        merchantRepository.existsByPhone(loginDto.getPhone()),
                                        null
                                ),
                                HttpStatus.OK);
                    } else {
                        user.get().setAccountNonLocked(true);
                        user.get().setLockTime(LocalDateTime.now());
                        userRepository.save(user.get());
                        return new ResponseEntity<>(
                                new JWTAuthResponse(
                                        null,
                                        true,
                                        "PIN Blocked. Please contact info@teleeza.africa or call +254706122122 / +254714627627 ",
                                        user.get().getEmail(),
                                        user.get().getStatus(),
                                        merchantRepository.existsByPhone(loginDto.getPhone()),
                                        null
                                ),
                                HttpStatus.OK);
                    }
                }
            }
        } else {
            log.info("User Present {}", user);
            return new ResponseEntity<>(
                    new JWTAuthResponse(
                            null,
                            true,
                            "Account does not exist",
                            "",
                            "",
                            merchantRepository.existsByPhone(loginDto.getPhone()),
                            null
                    ),
                    HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<?> registerUser(SignUpDto signUpDto) {
        String regex = "[0-9]+";
        String consecutiveNumbersRegex = "([0-9])\\1+";
        String obviousSequencesRegex = "1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321";

        // Check that the PIN contains only digits
        if (!signUpDto.getPassword().matches(regex)) {
            return new ResponseEntity<>(new Message("PIN should only contain digits", true), HttpStatus.OK);
        }

        // Check that the PIN is exactly 4 digits long
        if (signUpDto.getPassword().length() != 4) {
            return new ResponseEntity<>(new Message("PIN should be exactly 4 digits long", true), HttpStatus.OK);
        }

        // Check that the PIN does not have consecutive numbers e.g. 1111
        if (signUpDto.getPassword().matches(consecutiveNumbersRegex)) {
            return new ResponseEntity<>(new Message("PIN should not contain consecutive digits e.g 0000", true), HttpStatus.OK);
        }

        // Check for obvious sequences like 1234 or 4321
        if (signUpDto.getPassword().matches(obviousSequencesRegex)) {
            return new ResponseEntity<>(new Message("PIN should not be an obvious sequence like 1234 or 4321", true), HttpStatus.OK);
        }

        // Add check for username exists in a DB
        if (userRepository.existsByPhone(signUpDto.getPhone())) {
            return new ResponseEntity<>(new Message("Phone is already taken!", true), HttpStatus.OK);
        }

        // Add check for email exists in DB
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>(new Message("Email is already taken!", true), HttpStatus.OK);
        }

        if (signUpDto.getPhone().isEmpty()) {
            return new ResponseEntity<>(new Message("phone empty", true), HttpStatus.OK);
        }

        if (signUpDto.getPassword().isEmpty()) {
            return new ResponseEntity<>(new Message("password empty", true), HttpStatus.OK);
        }

        // Create user object
        User user = new User();
        user.setPhone(signUpDto.getPhone());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_MERCHANT");
        user.setRoles(Collections.singleton(roles));
        user.setStatus("INACTIVE");

        userRepository.save(user);

        return new ResponseEntity<>(new Message("Success", false), HttpStatus.OK);
    }

//    @Transactional
//    public ResponseEntity<?> registerUser(SignUpDto signUpDto) {
//        String pin = signUpDto.getPassword();
//
//        // PIN should be exactly 4-6 digits
//        if (!pin.matches("\\d{4,6}")) {
//            return new ResponseEntity<>(new Message("PIN must be 4 to 6 digits long", true), HttpStatus.OK);
//        }
//
//        // Prevent obvious repeated digits (0000, 1111, etc.)
//        if (pin.matches("^(\\d)\\1{3,5}$")) {
//            return new ResponseEntity<>(new Message("PIN should not contain repeated digits like 0000 or 111111", true), HttpStatus.OK);
//        }
//
//        // Prevent sequential PINs
//        List<String> commonSequences = Arrays.asList(
//                "1234", "2345", "3456", "4567", "5678", "6789", "9876", "8765", "7654", "6543", "5432", "4321"
//        );
//        if (commonSequences.contains(pin)) {
//            return new ResponseEntity<>(new Message("PIN should not be sequential numbers like 1234 or 4321", true), HttpStatus.OK);
//        }
//
//        // Check if phone already exists in DB
//        if (userRepository.existsByPhone(signUpDto.getPhone())) {
//            return new ResponseEntity<>(new Message("Phone is already taken!", true), HttpStatus.OK);
//        }
//
//        // Check if email already exists in DB
//        if (userRepository.existsByEmail(signUpDto.getEmail())) {
//            return new ResponseEntity<>(new Message("Email is already taken!", true), HttpStatus.OK);
//        }
//
//        if (signUpDto.getPhone().isEmpty()) {
//            return new ResponseEntity<>(new Message("Phone is empty", true), HttpStatus.OK);
//        }
//
//        if (signUpDto.getPassword().isEmpty()) {
//            return new ResponseEntity<>(new Message("Password is empty", true), HttpStatus.OK);
//        }
//
//        // Create user object
//        User user = new User();
//        user.setPhone(signUpDto.getPhone());
//        user.setEmail(signUpDto.getEmail());
//        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
//
//        Role roles = roleRepository.findByName("ROLE_MERCHANT");
//        user.setRoles(Collections.singleton(roles));
//        user.setStatus("INACTIVE");
//
//        userRepository.save(user);
//
//        return new ResponseEntity<>(new Message("Success", false), HttpStatus.OK);
//    }


//    @Transactional
//    public ResponseEntity<?> registerUser(SignUpDto signUpDto) {
//        String regex = "[0-9]+";
//        String consecutiveNumbersRegex = "([0-9])\\1+";
//
//
//        // check that the pin contains only digits
//        if (!signUpDto.getPassword().matches(regex)) {
//            return new ResponseEntity<>(new Message("PIN should only contain digits", true), HttpStatus.OK);
//        }
//
//        // check that the pin does not have consecutive numbers e.g. 1111
//        if (signUpDto.getPassword().matches(consecutiveNumbersRegex)) {
//            return new ResponseEntity<>(new Message("PIN should not contain consecutive digits e.g 0000", true), HttpStatus.OK);
//        }
//
//        // add check for username exists in a DB
//        if (userRepository.existsByPhone(signUpDto.getPhone())) {
//            return new ResponseEntity<>(new Message("Phone is already taken!", true), HttpStatus.OK);
//        }
//
//        // add check for email exists in DB
//        if (userRepository.existsByEmail(signUpDto.getEmail())) {
//            return new ResponseEntity<>(new Message("Email is already taken!", true), HttpStatus.OK);
//        }
//
//        if (signUpDto.getPhone().isEmpty()) {
//            return new ResponseEntity<>(new Message("phone empty", true), HttpStatus.OK);
//        }
//
//        if (signUpDto.getPassword().isEmpty()) {
//            return new ResponseEntity<>(new Message("password empty", true), HttpStatus.OK);
//        }
//
//        // create user object
//        User user = new User();
//        user.setPhone(signUpDto.getPhone());
//        user.setEmail(signUpDto.getEmail());
//        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
//
//        Role roles = roleRepository.findByName("ROLE_MERCHANT");
//        user.setRoles(Collections.singleton(roles));
//        // user.setRoles((Set<Role>) roles);
//        user.setStatus("INACTIVE");
//
//        /*sendEmailService.sendEmail(
//                "brian.onchari@teleeza.africa",
//                "Merchant Approval Request",
//                "This is a test email"
//        );*/
//        userRepository.save(user);
//
//
//        return new ResponseEntity<>(new Message("Success", false), HttpStatus.OK);
//    }

    @Transactional
    public ResponseEntity<?> resetPasswordRequest(PasswordResetRequestDto passwordResetRequestDto) {
        // check for use exists in DB
        Boolean existsByEmailAndPhone = userRepository.existsByEmailAndPhone(passwordResetRequestDto.getEmail(), passwordResetRequestDto.getPhone());
        if (existsByEmailAndPhone) {
            CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(
                    "+254" + passwordResetRequestDto.getPhone().substring(1)
            );
            customer.setOtpAttempts(0);
            customer.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
            customerRegistrationRepository.save(customer);
            // generate otp and save alongside user records
            // the otp as sms for user to use in password reset
            Random random = new Random();
            String otp = String.format("%04d", random.nextInt(10000));
            userRepository.saveOtp(otp, passwordResetRequestDto.getPhone(), passwordResetRequestDto.getEmail());
            advantaSmsApiImpl.sendSmsNotification(
                    "Dear " + customer.getFirstName() + ", use " + otp + " to reset your Teleeza PIN within 10 minutes.",
                    "254" + passwordResetRequestDto.getPhone().substring(1)
            );
            return new ResponseEntity<>(new Message("User Exists, proceed to password reset", false), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(new Message("User does not exist", true), HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<Message> passwordReset(PasswordResetDto passwordResetDto) {

        Boolean existsByPhoneEmailAndOtp = userRepository.existsByPhoneAndEmailAndOtp(
                passwordResetDto.getPhone(),
                passwordResetDto.getEmail(),
                passwordResetDto.getOtp()
        );

        CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(
                "+254" + passwordResetDto.getPhone().substring(1)
        );

        if (customer != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(customer.getOtpExpiryTime())) {
                return new ResponseEntity<>(new Message("OTP is expired. Request a New OTP", true), HttpStatus.OK);
            }
            if (customer.getOtpAttempts() >= 3) {
                return new ResponseEntity<>(new Message("You have exceeded the maximum number of attempts. Request a New OTP", true), HttpStatus.OK);
            }
            customer.setOtpAttempts(customer.getOtpAttempts() + 1);
            customerRegistrationRepository.save(customer);

            if (existsByPhoneEmailAndOtp) {
//            CustomerEntity customer = customerRegistrationRepository.findCustomerByPhoneNumber(
//                    "+254" + passwordResetDto.getPhone().substring(1)
//            );


                User user = new User();
                String password = passwordResetDto.getPassword();
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));
//            customer
                customer.setOtpAttempts(0);
                userRepository.updatePin(encodedPassword, passwordResetDto.getPhone(), passwordResetDto.getEmail(), passwordResetDto.getOtp());
                advantaSmsApiImpl.sendSmsNotification(
                        "Dear " + customer.getFirstName() + ", your Teleeza PIN was reset successfully",
                        "254" + passwordResetDto.getPhone().substring(1)
                );
                return new ResponseEntity<>(new Message("Password Reset successfully", false), HttpStatus.OK);
            } else {

                return new ResponseEntity<>(new Message("Records Don't Match", true), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Message("User not found.", true), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Message> changePin(ChangePinDto changePinDto) {
        Optional<User> user = userRepository.findByPhone(changePinDto.getPhone());
        if (passwordEncoder.matches(changePinDto.getOldPin(), user.get().getPassword())) {
            String regex = "[0-9]+";
            String consecutiveNumbersRegex = "([0-9])\\1+";

            // check that the pin contains only digits
            if (!changePinDto.getNewPin().matches(regex)) {
                return new ResponseEntity<>(new Message("PIN should only contain digits", true), HttpStatus.OK);
            }

            // check that the pin does not have consecutive numbers e.g. 1111
            if (changePinDto.getNewPin().matches(consecutiveNumbersRegex)) {
                return new ResponseEntity<>(new Message("PIN should not contain consecutive digits e.g 0000", true), HttpStatus.OK);
            }

            if (changePinDto.getPhone().isEmpty()) {
                return new ResponseEntity<>(new Message("phone empty", true), HttpStatus.OK);
            }

            if (changePinDto.getOldPin().isEmpty()) {
                return new ResponseEntity<>(new Message("password empty", true), HttpStatus.OK);
            }
            if (changePinDto.getNewPin().isEmpty()) {
                return new ResponseEntity<>(new Message("password empty", true), HttpStatus.OK);
            }

            String encodedPassword = passwordEncoder.encode(changePinDto.getNewPin());
            userRepository.changePin(encodedPassword, user.get().getPhone());
            return new ResponseEntity<>(new Message("Password changed", false), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("You have entered an incorrect Old PIN", true), HttpStatus.OK);
        }
    }
}
