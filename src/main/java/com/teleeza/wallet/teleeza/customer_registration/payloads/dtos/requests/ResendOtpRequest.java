package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests;

import lombok.Data;

@Data
public class ResendOtpRequest {
    private String phoneNumber;
    private String email;
}
