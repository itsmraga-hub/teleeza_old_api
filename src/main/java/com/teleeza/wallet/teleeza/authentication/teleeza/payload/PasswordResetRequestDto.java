package com.teleeza.wallet.teleeza.authentication.teleeza.payload;

import lombok.Data;

@Data
public class PasswordResetRequestDto {
    private String phone;
    private String email;
}
