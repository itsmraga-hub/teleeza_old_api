package com.teleeza.wallet.teleeza.authentication.teleeza.payload;

import lombok.Data;

@Data
public class LoginDto {
    private String phone;
    private String password;
    private String firebaseToken;
}
