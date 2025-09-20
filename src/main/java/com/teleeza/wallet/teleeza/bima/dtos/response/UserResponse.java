package com.teleeza.wallet.teleeza.bima.dtos.response;

import lombok.Data;

@Data
public class UserResponse {
    private String firstName;
    private String lastName;
    private String surname;
    private String phone;
    private Boolean isSubscribed;
    private String expirationDate;
}
