package com.teleeza.wallet.teleeza.authentication.teleeza.payload;

import lombok.Data;

@Data
public class ChangePinDto {
    private String phone;
    private String oldPin;
    private String newPin;
    private String email;

}
