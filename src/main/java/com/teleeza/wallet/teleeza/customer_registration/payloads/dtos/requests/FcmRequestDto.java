package com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.requests;

import lombok.Data;

@Data
public class FcmRequestDto {
    private String beneficiaryAccNo;
    private String fcmToken;
}
