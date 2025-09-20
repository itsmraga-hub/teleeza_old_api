package com.teleeza.wallet.teleeza.authentication.teleeza.controller;

import com.teleeza.wallet.teleeza.authentication.teleeza.payload.*;
import com.teleeza.wallet.teleeza.authentication.teleeza.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/user-auth")
@Slf4j
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        return authenticationService.authenticate(loginDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {
        return authenticationService.registerUser(signUpDto);
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> resetPasswordRequest(@RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        return authenticationService.resetPasswordRequest(passwordResetRequestDto);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Message> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        return authenticationService.passwordReset(passwordResetDto);
    }

    @PostMapping("/change-pin")
    public ResponseEntity<Message> changePin(@RequestBody ChangePinDto changePinDto) {
        return authenticationService.changePin(changePinDto);
    }


}

