package com.teleeza.wallet.teleeza.authentication.teleeza.payload;

import lombok.Data;

@Data
public class SignUpDto {
    private String phone;
    private String email;
    private String password;
    private String role;
}