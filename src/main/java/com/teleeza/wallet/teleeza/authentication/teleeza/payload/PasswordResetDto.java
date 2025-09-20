package com.teleeza.wallet.teleeza.authentication.teleeza.payload;

import lombok.Data;

@Data
public class PasswordResetDto {
    private String phone;
    private String email;
    private String password;
    private String otp;
}
